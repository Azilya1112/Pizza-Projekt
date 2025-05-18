package com.example.demo.grade.security;

import com.example.demo.grade.service.CartService;
import com.example.demo.grade.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final CartService cartService;
	private final UserService userService;
	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

	public CustomAuthenticationSuccessHandler(CartService cartService, UserService userService) {
		this.cartService = cartService;
		this.userService = userService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String username = authentication.getName();
		Long userId = userService.getUserIdByUsername(username);
		cartService.transferCartToUser(userId, request.getSession());

		authentication.getAuthorities().forEach(auth -> System.out.println("Role: " + auth.getAuthority()));

		String redirectURL = "/";
		if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
			redirectURL = "/admin"; // Перенаправление для администратора
		}
		response.sendRedirect(redirectURL);
	}
}