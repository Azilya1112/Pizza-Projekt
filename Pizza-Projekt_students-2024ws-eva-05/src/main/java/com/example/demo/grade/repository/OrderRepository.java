package com.example.demo.grade.repository;

import com.example.demo.grade.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    CharSequence findAllByUserId(Long id);
}

