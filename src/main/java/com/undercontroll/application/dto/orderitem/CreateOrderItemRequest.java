package com.undercontroll.application.dto.orderitem;

public record CreateOrderItemRequest(
        String brand,
        String model,
        String type,
        String imageUrl,
        String volt,
        String series,
        Double laborValue
) {}
