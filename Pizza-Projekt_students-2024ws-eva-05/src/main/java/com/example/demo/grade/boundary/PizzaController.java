package com.example.demo.grade.boundary;

import com.example.demo.grade.domain.Cart;
import com.example.demo.grade.domain.Pizza;
import com.example.demo.grade.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.grade.repository.CartRepository;
import com.example.demo.grade.repository.ItemRepository;
import com.example.demo.grade.service.CartService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpSession;
import com.example.demo.grade.service.UserService;

@Controller
public class PizzaController {

	private final CartService cartService;
	private final PizzaRepository pizzaRepository;
	private static final Logger logger = LoggerFactory.getLogger(PizzaController.class);
	private final UserService userService;

	@Autowired
	public PizzaController(PizzaRepository pizzaRepository, CartRepository cartRepository,
			ItemRepository itemRepository, CartService cartService, UserService userService) {
		this.pizzaRepository = pizzaRepository;
		this.userService = userService;
		this.cartService = cartService;
	}

	@GetMapping("/")
	public String getPizzas(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
		Iterable<Pizza> pizzas = pizzaRepository.findAll();

		model.addAttribute("pizzas", pizzas);

		if (userDetails != null) {
			model.addAttribute("username", userDetails.getUsername());
		}

		Cart cart;

		if (userDetails != null) {
			String username = userDetails.getUsername();
			Long userId = userService.getUserIdByUsername(username);
			cart = cartService.getCartForUser(userId);
			model.addAttribute("username", userDetails.getUsername());
		} else {
			cart = (Cart) session.getAttribute("cart");
			if (cart == null) {
				cart = new Cart();
				session.setAttribute("cart", cart);
			}
		}

		model.addAttribute("cartItemCount", cart.getItems().size());

		model.addAttribute("cart", cart);

		return "index";
	}

}