package com.example.demo.grade.boundary;

import com.example.demo.grade.service.OrderService;
import com.example.demo.grade.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class CustomerController {

	private final OrderService orderService;
	private final UserService userService;

    public CustomerController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/index")
	 public String grades(Model model) {
		  	return "index";
	 }

	@GetMapping("/einkaufsverlauf")
	public String viewOrderHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		return userService.handleOrderHistoryView(userDetails, model);
	}
}
