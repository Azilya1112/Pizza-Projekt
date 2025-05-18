package com.example.demo.grade.domain.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderDTO {
    private Long id;
    private LocalDateTime date;
    private String items;
    private Double totalPrice;
    private String deliveryAddress;
    private String status;
    private String formattedDate;
    private String expectedTime;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    public OrderDTO(Long id, LocalDateTime date, String items, Double totalPrice, String deliveryAddress, String status, int totalQuantity) {
        this.id = id;
        this.date = date;
        this.items = items;
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.formattedDate = date.format(formatter);

        int cookingTimeMinutes = totalQuantity * 10;
        int deliveryTimeMinutes = 20;
        LocalDateTime expectedDateTime = date.plusMinutes(cookingTimeMinutes + deliveryTimeMinutes);
        this.expectedTime = expectedDateTime.format(timeFormatter);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }



    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

}

