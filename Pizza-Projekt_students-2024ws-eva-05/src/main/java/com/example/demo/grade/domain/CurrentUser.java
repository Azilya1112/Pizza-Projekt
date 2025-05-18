package com.example.demo.grade.domain;

import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CurrentUser extends User {

	private Long userId;

	public CurrentUser(String username, String password, Long userId,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}