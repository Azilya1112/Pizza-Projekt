package com.example.demo.grade.service;

import com.example.demo.grade.domain.DeliveryAddress;
import com.example.demo.grade.domain.Item;
import com.example.demo.grade.domain.User;
import com.example.demo.grade.domain.dto.AccountResponseDTO;
import com.example.demo.grade.domain.dto.OrderDTO;
import com.example.demo.grade.domain.dto.TransferDTO;
import com.example.demo.grade.repository.DeliveryAddressRepository;
import com.example.demo.grade.repository.UserRepository;
import com.example.demo.grade.service.exception.PayUserException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestTemplate;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    
    @Value("${mpa.url}")
    private String mpaUrl;

    public UserService(UserRepository userRepository, DeliveryAddressRepository deliveryAddressRepository,
    		OrderService orderService, RestTemplate restTemplate) {
    	this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.deliveryAddressRepository = deliveryAddressRepository;
        this.orderService = orderService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void saveUser(User user) {
        userRepository.save(user); 

        try {
            createPayUserInMPA(user.getUsername()); 
        } catch (Exception e) {
            logger.error("Fehler bei der Synchronisierung mit MPA: {}", e.getMessage());
        }
    }



    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Attempting to load a user named: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new UsernameNotFoundException("Пользователь не найден");
                });
        logger.debug("Found User: {}", username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities()
        );
    }


    public String handleOrderHistoryView(UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        String username = userDetails.getUsername();
        List<OrderDTO> orderHistory = getOrderHistory(username);

        model.addAttribute("orderHistory", orderHistory);
        model.addAttribute("username", username);

        return "einkaufsverlauf";
    }

    public List<OrderDTO> getOrderHistory(String username) {
        User user = findByUsername(username);

        return orderService.getOrdersByUser(user.getId()).stream()
                .map(order -> {
                    int totalQuantity = order.getItems().stream()
                            .mapToInt(Item::getQuantity)
                            .sum();

                    return new OrderDTO(
                            order.getId(),
                            order.getOrderDate(),
                            order.getItems().stream()
                                    .map(item -> item.getPizza().getName() + " x" + item.getQuantity())
                                    .collect(Collectors.joining(", ")),
                            order.getTotalPrice(),
                            String.format("%s %s, %s %s",
                                    order.getDeliveryAddress().getStreet(),
                                    order.getDeliveryAddress().getHouseNumber(),
                                    order.getDeliveryAddress().getPostalCode(),
                                    order.getDeliveryAddress().getTown()),
                            order.getStatus().toString(),
                            totalQuantity
                    );
                })
                .toList();
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }


    @Transactional
    public void updateUserProfile(Long userId,
                                  String newUsername,
                                  String currentPassword,
                                  String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден."));


        if (newUsername != null && !newUsername.trim().isEmpty() && !newUsername.equals(user.getUsername())) {
            user.setUsername(newUsername);
        }

        if (currentPassword != null && !currentPassword.trim().isEmpty()
                && newPassword != null && !newPassword.trim().isEmpty()) {

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new IllegalArgumentException("Das aktuelle Passwort ist falsch");
            }

            if (!newPassword.equals(currentPassword)) {
                String encodedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedPassword);
            }
        }

        userRepository.save(user);
    }



    public boolean checkPassword(User user, String currentPassword) {
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }
    
    public void createPayUserInMPA(String username) {
        String url = mpaUrl + "/" + username + "/opened";

        try {
            restTemplate.put(url, null); 
            logger.info("Benutzer {} wurde erfolgreich im MPA angelegt", username);
        } catch (Exception e) {
            logger.error("Fehler beim Anlegen eines Benutzers in MPA: {}", e.getMessage());
        }
    }


    public String transferToPizzaBank(String username, double amount) throws PayUserException {
        String url = mpaUrl + "/" + username + "/payment";
        TransferDTO transferDTO = new TransferDTO("pizza_bank", amount);

        try {
            logger.info("Initiating payment from user '{}' to 'pizza_bank' with amount: {}", username, amount);

            ResponseEntity<AccountResponseDTO> response = restTemplate.postForEntity(url, transferDTO, AccountResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                logger.info("Payment successful: {}", response.getBody().getMessage());
                return "Transfer successful";
            } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                if (response.getBody() != null && response.getBody().getMessage() != null) {
                    String message = response.getBody().getMessage();
                    logger.warn("Payment failed: {}", message);
                    throw new PayUserException(message); // Throw exception for specific errors
                }
                throw new PayUserException("Transfer failed: Bad request");
            } else {
                throw new PayUserException("Transfer failed");
            }
        } catch (PayUserException e) {
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
            logger.error("Error during transfer from user '{}' to 'pizza_bank': {}", username, e.getMessage());
            throw new PayUserException("Transfer failed: " + e.getMessage());
        }
    }





}