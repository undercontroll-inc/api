package com.undercontroll.api.dto;

import com.undercontroll.api.model.OrderStatus;

public record CreateOrderItemRequest(
        String name,
        String imageUrl,
        String observation,
        String volt,
        String series,
        Double labor,
        OrderStatus status
) {}