package com.example.demo.user;


import com.example.demo.grade.domain.User;
import com.example.demo.grade.domain.dto.OrderDTO;
import com.example.demo.grade.repository.DeliveryAddressRepository;
import com.example.demo.grade.repository.UserRepository;
import com.example.demo.grade.service.OrderService;
import com.example.demo.grade.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private OrderService orderService;
    private Model model;
    private DeliveryAddressRepository deliveryAddressRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        deliveryAddressRepository = mock(DeliveryAddressRepository.class);
        orderService = mock(OrderService.class);
        model = mock(Model.class);
        RestTemplate restTemplate = mock(RestTemplate.class); 
        userService = new UserService(userRepository, deliveryAddressRepository, orderService, restTemplate);
    }
    @Test
    void saveUser_ShouldCallRepositorySave() {
        User user = new User();
        user.setUsername("testUser");

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserIdByUsername_ShouldReturnUserId() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Long userId = userService.getUserIdByUsername("testUser");

        assertEquals(1L, userId);
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void getUserIdByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUserIdByUsername("nonExistingUser"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonExistingUser");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setRoles(new ArrayList<>());

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("testPassword", userDetails.getPassword());
        verify(userRepository, times(1)).findByUsername("testUser");
    }


    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nonExistingUser"));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonExistingUser");
    }

    @Test
    void handleOrderHistoryView_ShouldRedirectToLogin_WhenUserDetailsIsNull() {
        String result = userService.handleOrderHistoryView(null, model);

        assertEquals("redirect:/login", result);
        verifyNoInteractions(model);
    }


    @Test
    void getOrderHistory_ShouldReturnOrderHistory() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        List<OrderDTO> mockOrderDTOList = new ArrayList<>();
        when(orderService.getOrdersByUser(1L)).thenReturn(new ArrayList<>());

        List<OrderDTO> orderHistory = userService.getOrderHistory("testUser");

        assertNotNull(orderHistory);
        assertEquals(mockOrderDTOList.size(), orderHistory.size());
        verify(orderService, times(1)).getOrdersByUser(1L);
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        User user = new User();
        user.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername("testUser");

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.findByUsername("nonExistingUser"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonExistingUser");
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(1L, foundUser.get().getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldReturnEmptyOptional_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isEmpty());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        User user = new User();
        user.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByUsername("testUser");

        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void getUserByUsername_ShouldReturnEmptyOptional_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserByUsername("nonExistingUser");

        assertTrue(foundUser.isEmpty());
        verify(userRepository, times(1)).findByUsername("nonExistingUser");
    }

    @Test
    void listUsers_ShouldReturnAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.listUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }
}
