package com.example.demo.grade.service;

import static org.mockito.Mockito.*;

import com.example.demo.grade.domain.User;
import com.example.demo.grade.domain.enums.Role;
import com.example.demo.grade.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import com.example.demo.grade.domain.DeliveryAddress;
import com.example.demo.grade.repository.DeliveryAddressRepository;
import com.example.demo.grade.service.OrderService;

import java.util.List;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeliveryAddressRepository deliveryAddressRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private RestTemplate restTemplate; 

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUserWithMPAIntegration() {

        User user = new User("testUser", "password", List.of(Role.USER));

        when(userRepository.save(user)).thenReturn(user);

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(String.class));
    }
}
