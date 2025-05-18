package com.example.demo.grade.service;

import com.example.demo.grade.domain.Cart;
import com.example.demo.grade.domain.Order;
import com.example.demo.grade.domain.DeliveryAddress;
import com.example.demo.grade.domain.User;
import com.example.demo.grade.domain.Pizza;
import com.example.demo.grade.domain.dto.AccountResponseDTO;
import com.example.demo.grade.domain.dto.TransferDTO;
import com.example.demo.grade.domain.enums.OrderStatus;
import com.example.demo.grade.repository.CartRepository;
import com.example.demo.grade.repository.ItemRepository;
import com.example.demo.grade.repository.OrderRepository;
import com.example.demo.grade.service.exception.PayUserException;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.demo.grade.repository.PizzaRepository;
import com.example.demo.grade.domain.enums.PizzaSize;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;
import com.example.demo.grade.domain.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final DeliveryAddressService deliveryAddressService;
    private final OrderRepository orderRepository;
    private final PizzaRepository pizzaRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    public CartService(CartRepository cartRepository, DeliveryAddressService deliveryAddressService,
                       OrderRepository orderRepository, UserService userService,
                       ItemRepository itemRepository, PizzaRepository pizzaRepository) {
        this.cartRepository = cartRepository;
        this.deliveryAddressService = deliveryAddressService;
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.pizzaRepository = pizzaRepository;
    }

    @Transactional
    public String viewCart(UserDetails userDetails, HttpSession session, Model model) {
        Cart cart;

        if (userDetails != null) {
            String username = userDetails.getUsername();
            Long userId = userService.getUserIdByUsername(username);
            cart = getCartForUser(userId);
            model.addAttribute("username", userDetails.getUsername());

            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
                session.setAttribute("cart", cart);
            }
        }

        cart.getItems().forEach(item -> logger.info("Item in cart: {} with ID: {}", item.getPizza().getName(), item.getId()));

        int totalQuantity = cart.getItems().stream()
                .mapToInt(Item::getQuantity)
                .sum();
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("cart", cart);

        return "warenkorb";
    }

    @Transactional
    public String addToCartHandler(Long pizzaId, int quantity, PizzaSize size, UserDetails userDetails, HttpSession session) {
        if (userDetails != null) {
            String username = userDetails.getUsername();
            Long userId = userService.getUserIdByUsername(username);
            addToCart(userId, pizzaId, quantity, size, session);
        } else {
            addToCartForSession(pizzaId, quantity, size, session);
        }

        if (userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/pizzas";
        }

        return "redirect:/";
    }

    @Transactional
    public String removeFromCartHandler(Long itemId, UserDetails userDetails, HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        if (userDetails != null) {
            Long userId = userService.getUserIdByUsername(userDetails.getUsername());
            removeItemFromCart(userId, itemId, session);
        } else {
            removeItemFromCart(null, itemId, session);
        }

        redirectAttributes.addFlashAttribute("success", "Artikel wurde erfolgreich entfernt!");
        return "redirect:/warenkorb";
    }


    public Cart getCartForUser(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }
    
    @Transactional
    public void addToCart(Long userId, Long pizzaId, int quantity, PizzaSize size, HttpSession session) {
        Pizza pizza = pizzaRepository.findById(pizzaId).orElseThrow(() -> {
            throw new RuntimeException("Pizza not found");
        });

        double basePrice = pizza.getBasePrice();
        switch (size) {
            case SMALL:
                basePrice -= 2;
                break;
            case LARGE:
                basePrice += 2;
                break;
            default:
                break;
        }

        double totalPrice = basePrice * quantity;

        Cart cart = getCartForUser(userId);

        Item item = new Item();
        item.setPizza(pizza);
        item.setQuantity(quantity);
        item.setSize(size);
        item.setPrice(totalPrice);

        cart.addItem(item);

        double totalCartPrice = cart.getItems().stream().mapToDouble(Item::getPrice).sum();
        cart.setTotalPrice(totalCartPrice);

        cartRepository.save(cart);
    }
    
    @Transactional
    public void addToCartForSession(Long pizzaId, int quantity, PizzaSize size, HttpSession session) {

        Pizza pizza = pizzaRepository.findById(pizzaId).orElseThrow(() -> {
            throw new RuntimeException("Pizza not found");
        });

        double basePrice = pizza.getBasePrice();
        switch (size) {
            case SMALL:
                basePrice -= 2;
                break;
            case LARGE:
                basePrice += 2;
                break;
            default:
                break;
        }

        double totalPrice = basePrice * quantity;

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        Item item = new Item();
        item.setId(generateUniqueId());
        item.setPizza(pizza);		
        item.setQuantity(quantity);
        item.setSize(size);
        item.setPrice(totalPrice);

        cart.addItem(item);

        double totalCartPrice = cart.getItems().stream().mapToDouble(Item::getPrice).sum();
        cart.setTotalPrice(totalCartPrice);
    }
    
    private Long generateUniqueId() {
        return System.currentTimeMillis(); 
    }
    
    @Transactional
    public void save(Cart cart) {
        cartRepository.save(cart);
    }
    
    @Transactional
    public void transferCartToUser(Long userId, HttpSession session) {
        Cart sessionCart = (Cart) session.getAttribute("cart");
        logger.debug("Session cart: {}", sessionCart);

        if (sessionCart != null && userId != null) {
            Cart userCart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Cart cart = new Cart();
                        cart.setUserId(userId);
                        return cartRepository.save(cart);
                    });

            logger.debug("User cart before items are added: {}", userCart);

            double totalPrice = 0.0;

            for (Item item : sessionCart.getItems()) {
                logger.debug("Item being added: {}", item);

                item.setCart(userCart);
                userCart.getItems().add(item);

                totalPrice += item.getPrice();
            }

            userCart.setTotalPrice(totalPrice);

            logger.debug("User cart after items are added: {}", userCart);

            cartRepository.save(userCart);

            session.removeAttribute("cart");
        }
    }
    
    @Transactional	
    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }

    public String kaufenPage(UserDetails userDetails, HttpSession session, Model model) {
        Cart cart;

        if (userDetails != null) {
            String username = userDetails.getUsername();
            Long userId = userService.getUserIdByUsername(username);

            cart = getCartForUser(userId);
            model.addAttribute("username", userDetails.getUsername());

            List<DeliveryAddress> deliveryAddresses = deliveryAddressService.getAddressesForUser(userId);
            model.addAttribute("deliveryAddresses", deliveryAddresses);

            int totalQuantity = cart.getItems().stream()
                    .mapToInt(Item::getQuantity)
                    .sum();

            if (totalQuantity == 0) {
                return "redirect:/warenkorb";
            }
            model.addAttribute("totalQuantity", totalQuantity);
        } else {
            cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
                session.setAttribute("cart", cart);
            }
            if (cart.getItems().isEmpty()) {
                return "redirect:/warenkorb";
            }
            model.addAttribute("deliveryAddresses", List.of());
        }

        model.addAttribute("cart", cart);
        return "kaufen";
    }


    @Transactional
    public String saveNewAddress(String street, String houseNumber, String postalCode, String town,
                                 UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.getUserByUsername(userDetails.getUsername());

        DeliveryAddress newAddress = new DeliveryAddress(postalCode, town, houseNumber, street);
        newAddress.setUser(user.orElse(null));
        DeliveryAddress savedAddress = deliveryAddressService.save(newAddress);

        redirectAttributes.addFlashAttribute("selectedAddressId", savedAddress.getId());
        redirectAttributes.addFlashAttribute("success", "Adresse erfolgreich hinzugefügt!");
        return "redirect:/kaufen";
    }

    @Transactional
    public String checkout(Long selectedAddressDTOid, UserDetails userDetails) {
        logger.info("Start der Methode checkout");

        Optional<User> optionalUser = userService.getUserByUsername(userDetails.getUsername());
        User user = optionalUser.get();
        logger.info("User found: {}", user.getUsername());

        Cart cart = getCartForUser(user.getId());

        DeliveryAddress deliveryAddress = deliveryAddressService.getAddressById(selectedAddressDTOid);

        // Perform payment
        try {
            String transferResult = userService.transferToPizzaBank(user.getUsername(), cart.getTotalPrice());
            if (!"Transfer successful".equals(transferResult)) {
                logger.error("Payment failed for user '{}': {}", user.getUsername(), transferResult);
                return "redirect:/kaufen?error=transfer_failed";
            }
        } catch (PayUserException e) {
            if (e.getMessage().contains("Insufficient funds")) {
                logger.warn("Insufficient funds for user: {}", user.getUsername());
                return "redirect:/kaufen?error=insufficient_funds";
            }
            logger.error("Payment failed for user '{}': {}", user.getUsername(), e.getMessage());
            return "redirect:/kaufen?error=transfer_failed";
        }

        Order order = new Order(
                LocalDateTime.now(),
                cart.getTotalPrice(),
                user,
                new ArrayList<>(),
                deliveryAddress
        );
        order.setStatus(OrderStatus.BOUGHT);

        order = orderRepository.save(order);
        logger.info("Order successfully created with ID: {}", order.getId());

        for (Item item : cart.getItems()) {
            item.setCart(null);
            item.setOrder(order);
            itemRepository.save(item);
        }

        clearCart(cart);
        logger.info("Cart cleared");

        return "redirect:/";
    }




    @Transactional
    public void removeItemFromCart(Long userId, Long itemId, HttpSession session) {

        if (userId != null) {
            Cart cart = getCartForUser(userId);  
            if (cart != null) {
                logger.info("Removing item from cart for user: {}", userId);
                
                cart.getItems().removeIf(item -> item.getId().equals(itemId));

                double totalPrice = cart.getItems().stream()
                                        .mapToDouble(Item::getPrice)
                                        .sum();
                cart.setTotalPrice(totalPrice);

                cartRepository.save(cart);
                itemRepository.deleteById(itemId);  

                session.setAttribute("cart", cart);
                logger.info("Item removed from cart and database, session updated.");
            }
        } else {

            Cart sessionCart = (Cart) session.getAttribute("cart");
            if (sessionCart != null) {
                logger.info("Removing item from session cart");
                logger.info("Session Cart contents: {}", sessionCart);
 
                sessionCart.getItems().removeIf(item -> item.getId().equals(itemId));


                double totalPrice = sessionCart.getItems().stream()
                                               .mapToDouble(Item::getPrice)
                                               .sum();
                sessionCart.setTotalPrice(totalPrice);

                session.setAttribute("cart", sessionCart);
                logger.info("Item removed from session cart. Total price recalculated.");
            }
        }
    }



}