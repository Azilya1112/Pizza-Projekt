package com.example.demo.grade.repository;

import com.example.demo.grade.domain.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {


}
