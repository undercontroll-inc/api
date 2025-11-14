package com.undercontroll.api.dto;

public record CreateOrderItemRequest(
        String brand,
        String model,
        String type,
        String imageUrl,
        String observation,
        String volt,
        String series,
        Double laborValue
) {}