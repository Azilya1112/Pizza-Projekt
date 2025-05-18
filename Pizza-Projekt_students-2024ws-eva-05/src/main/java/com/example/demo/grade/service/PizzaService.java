package com.example.demo.grade.service;

import com.example.demo.grade.domain.Pizza;
import com.example.demo.grade.repository.PizzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;



@Service
public class PizzaService {

	private final PizzaRepository pizzaRepository;
	private final FilesStorageService filesStorageService;

	public PizzaService(PizzaRepository pizzaRepository, FilesStorageService filesStorageService) {
		this.pizzaRepository = pizzaRepository;
		this.filesStorageService = filesStorageService;
	}


	public List<Pizza> getAllPizzas() {
		return pizzaRepository.findAll();
	}

	public Optional<Pizza> getPizzaById(Long id) {
		return pizzaRepository.findById(id);

	}

	public void savePizza(Pizza pizza) {
		pizzaRepository.save(pizza);
	}
	
	@Transactional
	public void updatePizza(Long id, String name, double basePrice, MultipartFile imageFile) {
		Pizza pizza = pizzaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid pizza ID: " + id));

		pizza.setName(name);
		pizza.setBasePrice(basePrice);

		if (imageFile != null && !imageFile.isEmpty()) {

			if (pizza.getImage() != null && !pizza.getImage().isEmpty()) {
				filesStorageService.delete(pizza.getImage());
			}

			String imagePath = filesStorageService.save(imageFile);
			pizza.setImage(imagePath);
		}

		pizzaRepository.save(pizza);
	}
	
	@Transactional
	public void deletePizza(Long id) {
		pizzaRepository.deleteById(id);
	}





}
