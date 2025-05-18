package com.example.demo.cart;


import com.example.demo.grade.boundary.CartController;
import com.example.demo.grade.domain.Cart;
import com.example.demo.grade.domain.Pizza;
import com.example.demo.grade.repository.OrderRepository;
import com.example.demo.grade.repository.PizzaRepository;
import com.example.demo.grade.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CartController.class)
@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private PizzaRepository pizzaRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private DeliveryAddressService deliveryAddressService;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void userShouldAccessCartPage() throws Exception {
      
        when(userService.getUserIdByUsername("user")).thenReturn(1L);
        Cart mockCart = new Cart();
        when(cartService.getCartForUser(1L)).thenReturn(mockCart);

  
        mockMvc.perform(get("/warenkorb"))
                .andExpect(status().isOk()) 
                .andExpect(view().name("warenkorb"))
                .andExpect(model().attributeExists("cart")) 
                .andExpect(model().attribute("cart", mockCart)); 
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminShouldAccessCartPage() throws Exception {

        when(userService.getUserIdByUsername("admin")).thenReturn(1L);
        Cart mockCart = new Cart();
        when(cartService.getCartForUser(1L)).thenReturn(mockCart);

        mockMvc.perform(get("/warenkorb"))
                .andExpect(status().isOk())
                .andExpect(view().name("warenkorb"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attribute("cart", mockCart));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void userShouldCheckoutOrder() throws Exception {
        when(cartService.checkout(Mockito.anyLong(), Mockito.any())).thenReturn("redirect:/");

        mockMvc.perform(post("/checkout")
                        .param("selectedAddressDTOid", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));


        verify(cartService, times(1)).checkout(Mockito.eq(1L), Mockito.any());
    }


}
