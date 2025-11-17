package com.undercontroll.api.dto;

public record UpdateComponentRequest(
        String item,
        String description,
        String brand,
        Double price,
        String supplier,
        String category
) {
}
