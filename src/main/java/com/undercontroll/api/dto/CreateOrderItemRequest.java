package com.undercontroll.api.dto;

import com.undercontroll.api.model.OrderItemStatus;

public record CreateOrderItemRequest(
        String name,
        String imageUrl,
        String observation,
        String volt,
        String series,
        Double labor,
        OrderItemStatus status
) {}