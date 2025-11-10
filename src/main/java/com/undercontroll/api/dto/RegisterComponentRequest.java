package com.undercontroll.api.dto;

public record RegisterComponentRequest(
        String name,
        String description,
        String brand,
        Double price,
        String supplier,
        String category,
        Long quantity
) {
}
