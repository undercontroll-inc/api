package com.undercontroll.api.dto;

public record RegisterComponentRequest(
        String item,
        String description,
        String brand,
        String category,
        Long quantity,
        Double price,
        String supplier
) {
}
