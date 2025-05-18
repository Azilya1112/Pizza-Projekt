package com.example.demo.cart;


import com.example.demo.grade.domain.Cart;
import com.example.demo.grade.domain.DeliveryAddress;
import com.example.demo.grade.domain.Item;
import com.example.demo.grade.domain.Order;
import com.example.demo.grade.domain.User;
import com.example.demo.grade.repository.CartRepository;
import com.example.demo.grade.repository.OrderRepository;
import com.example.demo.grade.service.CartService;
import com.example.demo.grade.service.DeliveryAddressService;
import com.example.demo.grade.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private DeliveryAddressService deliveryAddressService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCartForUser_ShouldReturnExistingCart() {
        Long userId = 1L;
        Cart existingCart = new Cart();
        existingCart.setUserId(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existingCart));

        Cart cart = cartService.getCartForUser(userId);

        assertNotNull(cart);
        assertEquals(userId, cart.getUserId());
        verify(cartRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getCartForUser_ShouldCreateNewCartIfNotFound() {
        Long userId = 1L;
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart cart = cartService.getCartForUser(userId);

        assertNotNull(cart);
        assertEquals(userId, cart.getUserId());
        verify(cartRepository, times(1)).findByUserId(userId);
        verify(cartRepository, times(1)).save(cart);
    }


    @Test
    void save_ShouldSaveCart() {
        Cart cart = new Cart();
        cartService.save(cart);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void transferCartToUser_ShouldMergeSessionCartIntoUserCart() {
        Long userId = 1L;
        Cart sessionCart = new Cart();
        Item item = new Item();
        item.setPrice(10.0);
        sessionCart.getItems().add(item);

        Cart userCart = new Cart();
        userCart.setUserId(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(userCart));

        cartService.transferCartToUser(userId, createHttpSessionWithCart(sessionCart));

        assertEquals(1, userCart.getItems().size());
        assertEquals(10.0, userCart.getTotalPrice());
        verify(cartRepository, times(1)).save(userCart);
    }

    @Test
    void clearCart_ShouldClearItemsAndResetTotalPrice() {
        Cart cart = new Cart();
        cart.getItems().add(new Item());
        cart.setTotalPrice(100.0);

        cartService.clearCart(cart);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getTotalPrice());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void saveNewAddress_ShouldCreateNewAddressAndRedirectToKaufen() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        User user = new User();
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        DeliveryAddress newAddress = new DeliveryAddress();
        when(deliveryAddressService.save(any(DeliveryAddress.class))).thenReturn(newAddress);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String result = cartService.saveNewAddress("Street", "12", "12345", "Town", userDetails, redirectAttributes);

        assertEquals("redirect:/kaufen", result);
        verify(deliveryAddressService, times(1)).save(any(DeliveryAddress.class));
    }


    @Test
    void checkout_ShouldRedirectToKaufenIfNoUserFound() {
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.empty());

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String result = cartService.checkout(1L, userDetails);

        assertEquals("redirect:/kaufen", result);
    }

    @Test
    void checkout_ShouldRedirectToKaufenIfNoAddressProvided() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        User user = new User();
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        Cart cart = new Cart();
        cart.getItems().add(new Item());
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));

        String result = cartService.checkout(null, userDetails);

        assertEquals("redirect:/kaufen", result);
    }

    @Test
    void checkout_ShouldRedirectToKaufenIfCartIsEmpty() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        User user = new User();
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        Cart cart = new Cart();
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));

        String result = cartService.checkout(1L, userDetails);

        assertEquals("redirect:/kaufen", result);
    }

    @Test
    void checkout_ShouldRedirectToKaufenIfAddressNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        User user = new User();
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        Cart cart = new Cart();
        cart.getItems().add(new Item());
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));

        when(deliveryAddressService.getAddressById(anyLong())).thenReturn(null);

        String result = cartService.checkout(1L, userDetails);

        assertEquals("redirect:/kaufen", result);
    }

    private HttpSession createHttpSessionWithCart(Cart cart) {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("cart")).thenReturn(cart);
        return session;
    }
}

