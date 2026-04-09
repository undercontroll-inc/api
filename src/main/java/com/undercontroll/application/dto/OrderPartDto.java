package com.undercontroll.application.dto;

public record OrderPartDto(
        Integer componentId,
        String name,
        String description,
        String brand,
        Double price,
        String supplier,
        String category,
        Long demandQuantity,
        Long componentStock
) {
}

