package com.example.demo.grade.domain.dto;

public record AccountResponseDTO (String code) {
    public String getMessage() {
        return code;
    }
}