package com.undercontroll.application.dto.component;

public record ComponentDto(
        Integer id,
        String item,
        String description,
        String brand,
        Double price,
        Long quantity,
        String supplier,
        String category
) {
}
