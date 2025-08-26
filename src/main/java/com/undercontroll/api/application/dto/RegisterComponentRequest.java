package com.undercontroll.api.application.dto;

public record RegisterComponentRequest(
        String name,
        String description,
        String brand,
        Double price,
        String supplier,
        String category
) {
}
