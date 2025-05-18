package com.example.demo.pizza;

import com.example.demo.grade.domain.Pizza;
import com.example.demo.grade.repository.PizzaRepository;
import com.example.demo.grade.service.FilesStorageService;
import com.example.demo.grade.service.PizzaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PizzaServiceTest {

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private FilesStorageService filesStorageService;

    @InjectMocks
    private PizzaService pizzaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllPizzas() {
        List<Pizza> pizzas = List.of(
                new Pizza(1L, "Margherita", 9.99, "/images/margherita.png"),
                new Pizza(2L, "Pepperoni", 10.99, "/images/pepperoni.png")
        );

        when(pizzaRepository.findAll()).thenReturn(pizzas);

        List<Pizza> result = pizzaService.getAllPizzas();

        assertEquals(2, result.size());
        verify(pizzaRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnPizzaById() {
        Pizza pizza = new Pizza(1L, "Margherita", 9.99, "/images/margherita.png");

        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));

        Optional<Pizza> result = pizzaService.getPizzaById(1L);

        assertTrue(result.isPresent());
        assertEquals("Margherita", result.get().getName());
        verify(pizzaRepository, times(1)).findById(1L);
    }

    @Test
    void shouldSavePizza() {
        Pizza pizza = new Pizza(1L, "Margherita", 9.99, "/images/margherita.png");

        pizzaService.savePizza(pizza);

        verify(pizzaRepository, times(1)).save(pizza);
    }

    @Test
    void shouldUpdatePizza() {
        Pizza pizza = new Pizza(1L, "Margherita", 9.99, "/images/margherita.png");

        MultipartFile imageFile = mock(MultipartFile.class);
        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
        when(imageFile.isEmpty()).thenReturn(false);
        when(filesStorageService.save(imageFile)).thenReturn("/images/new_image.png");

        pizzaService.updatePizza(1L, "Updated Pizza", 15.99, imageFile);

        assertEquals("Updated Pizza", pizza.getName());
        assertEquals(15.99, pizza.getBasePrice());
        assertEquals("/images/new_image.png", pizza.getImage());
        verify(pizzaRepository, times(1)).save(pizza);
    }

    @Test
    void shouldDeletePizza() {
        pizzaService.deletePizza(1L);
        verify(pizzaRepository, times(1)).deleteById(1L);
    }
}
