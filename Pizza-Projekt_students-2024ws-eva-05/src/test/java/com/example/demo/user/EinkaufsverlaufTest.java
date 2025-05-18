package com.example.demo.user;

import com.example.demo.grade.domain.User;
import com.example.demo.grade.domain.enums.OrderStatus;
import com.example.demo.grade.repository.DeliveryAddressRepository;
import com.example.demo.grade.repository.OrderRepository;
import com.example.demo.grade.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;

import jakarta.transaction.Transactional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.demo.grade.domain.DeliveryAddress;
import com.example.demo.grade.domain.Order;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EinkaufsverlaufTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Test
    public void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void testOrderPersistence() {
        User user = userRepository.findByUsername("bnutz").orElseThrow();

        DeliveryAddress deliveryAddress = new DeliveryAddress("12345", "Berlin", "10", "Test Street");
        deliveryAddress.setUser(user);
        deliveryAddress = deliveryAddressRepository.save(deliveryAddress);

        Order order = new Order(LocalDateTime.now(), 50.75, user, new ArrayList<>(), deliveryAddress);
        order.setStatus(OrderStatus.BOUGHT);
        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder.getId(), "Order ID should be generated");
        assertEquals(50.75, savedOrder.getTotalPrice(), "Total price should match");
        assertEquals(OrderStatus.BOUGHT, savedOrder.getStatus(), "Order status should match");
        assertEquals(user.getId(), savedOrder.getUser().getId(), "Order user should match");
        assertEquals(deliveryAddress.getId(), savedOrder.getDeliveryAddress().getId(), "Order delivery address should match");
    }



}
