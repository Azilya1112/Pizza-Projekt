package com.example.demo.grade.repository;

import com.example.demo.grade.domain.DeliveryAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.grade.domain.User;
import com.example.demo.grade.domain.Pizza;
import com.example.demo.grade.domain.enums.Role;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PizzaRepository pizzaRepository;  

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        User user1 = new User("bnutz", passwordEncoder.encode("n1"), List.of(Role.USER));
        User user2 = new User("cnutz", passwordEncoder.encode("n2"), List.of(Role.USER));
        User admin = new User("admin", passwordEncoder.encode("a1"), List.of(Role.ADMIN));

        DeliveryAddress address1 = new DeliveryAddress("10115", "Berlin", "123A", "Main Street");
        DeliveryAddress address2 = new DeliveryAddress("20234", "Hamburg", "45B", "Ocean Avenue");
        user1.addDeliveryAddress(address1);
        user1.addDeliveryAddress(address2);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(admin);

        pizzaRepository.save(new Pizza("Margherita", 6.99, "/images/margherita.png"));
        pizzaRepository.save(new Pizza("Pepperoni", 7.99, "/images/pepperoni.png"));
        pizzaRepository.save(new Pizza("Vegeterian", 5.99, "/images/vegeterian.png"));
        pizzaRepository.save(new Pizza("Barbecue", 8.99, "/images/barbecue.png"));

    }
}