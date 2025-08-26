package com.undercontroll.api.application.dto;

public record UpdateComponentRequest(
        Integer id,
        String name,
        String description,
        String brand,
        Double price,
        String supplier,
        String category
) {
}
