package com.example.demo.grade.boundary;

import com.example.demo.grade.domain.Cart;
import com.example.demo.grade.service.CartService;
import com.example.demo.grade.service.FilesStorageService;
import com.example.demo.grade.service.PizzaService;
import com.example.demo.grade.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.demo.grade.domain.Pizza;
import com.example.demo.grade.repository.UserRepository;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Controller
@RequestMapping("/admin")
public class AdminController {

	private final PizzaService pizzaService;
	private final UserRepository userRepository;
	private final UserService userService;
	private final FilesStorageService filesStorageService;
	private final CartService cartService;

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


	public AdminController(PizzaService pizzaService, UserRepository userRepository, UserService userService, FilesStorageService filesStorageService, CartService cartService) {
		this.pizzaService = pizzaService;
		this.userRepository = userRepository;
		this.userService = userService;
		this.filesStorageService = filesStorageService;
		this.cartService=cartService;
	}

	@GetMapping("")
	public  String adminHome() {
		return "redirect:/admin/pizzas";
	}

	@GetMapping("/pizzas")
	public String listPizzas(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
		model.addAttribute("pizzas", pizzaService.getAllPizzas());

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

		return "admin-main";
	}

	@GetMapping({ "/warenkorb"})
	public String viewCart(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, Model model) {
		Cart cart;

		if (userDetails != null) {

			String username = userDetails.getUsername();
			Long userId = userService.getUserIdByUsername(username);
			cart = cartService.getCartForUser(userId);
			model.addAttribute("username", userDetails.getUsername());

			boolean isAdmin = userDetails.getAuthorities().stream()
					.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
			model.addAttribute("isAdmin", isAdmin);

		} else {

			cart = (Cart) session.getAttribute("cart");
			if (cart == null) {
				cart = new Cart();
				session.setAttribute("cart", cart);
			}
		}

		model.addAttribute("cart", cart);
		return "warenkorb";
	}

	@GetMapping("/pizzas/new")
	public String newPizzaForm(Model model) {
		model.addAttribute("pizza", new Pizza());
		return "admin-new-pizza";
	}

	@PostMapping("/pizzas")
	public String savePizza(@RequestParam("name") String name,
							@RequestParam("basePrice") double basePrice,
							@RequestParam("image") MultipartFile imageFile) {
		String imagePath = filesStorageService.save(imageFile); 
		Pizza pizza = new Pizza();
		pizza.setName(name);
		pizza.setBasePrice(basePrice);
		pizza.setImage(imagePath);


		pizzaService.savePizza(pizza);
		return "redirect:/admin/pizzas";
	}

	@GetMapping("/image/{filename}")
	public ResponseEntity<Resource> getImage(@PathVariable String filename) {
		Resource file = filesStorageService.load(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@PostMapping("/pizzas/delete/{id}")
	public String deletePizza(@PathVariable Long id) {
		Pizza pizza = pizzaService.getPizzaById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid pizza ID: " + id));
		filesStorageService.delete(pizza.getImage());
		pizzaService.deletePizza(id);
		return "redirect:/admin/pizzas";
	}

	@GetMapping("/pizzas/edit/{id}")
	public String editPizzaForm(@PathVariable Long id, Model model) {
		Pizza pizza = pizzaService.getPizzaById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid pizza ID: " + id));
		model.addAttribute("pizza", pizza);
		return "admin-pizza-edit";
	}

	@PostMapping("/pizzas/update/{id}")
	public String updatePizza(@PathVariable Long id,
							  @RequestParam("name") String name,
							  @RequestParam("basePrice") double basePrice,
							  @RequestParam(value = "image", required = false) MultipartFile imageFile) {
		pizzaService.updatePizza(id, name, basePrice, imageFile);
		return "redirect:/admin/pizzas";
	}


    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "list-users";
    }

}
