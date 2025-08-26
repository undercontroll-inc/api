package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.enums.OrderItemStatus;

public record CreateOrderItemRequest(
        String name,
        String imageUrl,
        Integer orderId,
        Double price,
        Double discount,
        Integer quantity,
        OrderItemStatus status
) {}