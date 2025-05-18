package com.example.demo.grade.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.example.demo.grade.domain.CurrentUser;

@Component
public class CurrentUserProvider {

	public CurrentUser getCurrentUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser)) {
			throw new RuntimeException("No authenticated user found");
		}
		return (CurrentUser) authentication.getPrincipal();
	}
}
