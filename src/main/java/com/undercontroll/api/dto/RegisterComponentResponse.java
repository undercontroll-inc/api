package com.undercontroll.api.dto;

public record RegisterComponentResponse(
        String name,
        String description,
        String brand,
        Double price,
        String supplier,
        String category
) {
}
