package com.example.demo.grade.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "pizza")
public class Pizza {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private double basePrice;

	@Column(nullable = false)
	private String image;

	public Pizza() {
	}

	public Pizza(String name, double basePrice, String image) {
		this.name = name;
		this.basePrice = basePrice;
		this.image = image;
	}
	
	public Pizza(Long id,String name, double basePrice, String image) {
		  this.id = id;
		  this.name = name;
		  this.basePrice = basePrice;
		  this.image = image;
		 }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
