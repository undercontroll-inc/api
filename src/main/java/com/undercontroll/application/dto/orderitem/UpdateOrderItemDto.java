package com.undercontroll.application.dto.orderitem;

public record UpdateOrderItemDto(
        Integer id,
        String type,
        String brand,
        String model,
        String volt,
        String series,
        Double laborValue,
        String customerNote
) {
}
