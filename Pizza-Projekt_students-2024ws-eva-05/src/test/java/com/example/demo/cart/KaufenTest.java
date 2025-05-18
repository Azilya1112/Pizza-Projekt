package com.example.demo.cart;

import com.example.demo.grade.domain.*;
import com.example.demo.grade.repository.CartRepository;
import com.example.demo.grade.repository.OrderRepository;
import com.example.demo.grade.repository.PizzaRepository;
import com.example.demo.grade.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


@SpringBootTest
@AutoConfigureMockMvc
public class KaufenTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PizzaRepository pizzaRepository;


    @Transactional
    @Test
    public void testSaveNewAddress() throws Exception {
        User user = userRepository.findByUsername("bnutz").orElseThrow();

        mockMvc.perform(post("/neue-address")
                        .with(user(user.getUsername()).roles("USER"))
                        .param("street", "New Street")
                        .param("houseNumber", "45C")
                        .param("postalCode", "30345")
                        .param("town", "Munich"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/kaufen"));

        User updatedUser = userRepository.findByUsername("bnutz").orElseThrow();
        assertTrue(updatedUser.getDeliveryAddresses().stream()
                .anyMatch(address -> "New Street".equals(address.getStreet())));
    }



}


