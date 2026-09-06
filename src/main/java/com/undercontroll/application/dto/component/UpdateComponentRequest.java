package com.undercontroll.application.dto.component;

public record UpdateComponentRequest(
        String item,
        String description,
        String brand,
        Double price,
        String supplier,
        String category
) {
}
