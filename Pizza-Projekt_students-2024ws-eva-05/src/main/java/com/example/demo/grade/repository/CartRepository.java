package com.example.demo.grade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.grade.domain.Cart;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findByUserId(Long userId);

}
