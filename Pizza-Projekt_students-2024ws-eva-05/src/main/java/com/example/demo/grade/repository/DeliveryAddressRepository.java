package com.example.demo.grade.repository;

import com.example.demo.grade.domain.DeliveryAddress;
import com.example.demo.grade.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    List<DeliveryAddress> findByUserId(Long userId);
}


