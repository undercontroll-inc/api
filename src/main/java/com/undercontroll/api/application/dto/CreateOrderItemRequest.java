package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.enums.OrderItemStatus;

public record CreateOrderItemRequest(
        String name,
        String imageUrl,
        String observation,
        String volt,
        String series,
        Double labor,
        OrderItemStatus status
) {}