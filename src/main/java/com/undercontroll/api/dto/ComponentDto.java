package com.undercontroll.api.dto;

public record ComponentDto(
        String name,
        String description,
        String brand,
        Double price,
        String supplier,
        String category
) {
}
