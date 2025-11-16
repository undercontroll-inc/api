package com.undercontroll.api.dto;

import com.undercontroll.api.model.OrderStatus;

import java.time.LocalDateTime;

public record UpdateOrderItemRequest(
        Integer id,
        String imageUrl,
        Double labor,
        String observation,
        String volt,
        String series,
        LocalDateTime completedAt
) {
}
