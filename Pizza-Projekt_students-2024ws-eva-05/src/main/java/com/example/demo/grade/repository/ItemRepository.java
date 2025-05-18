package com.example.demo.grade.repository;

import com.example.demo.grade.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
