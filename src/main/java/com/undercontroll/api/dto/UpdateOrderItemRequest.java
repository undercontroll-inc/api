package com.undercontroll.api.dto;

import com.undercontroll.api.model.OrderStatus;

import java.time.LocalDateTime;

public record UpdateOrderItemRequest(
        Integer id,
        String name,
        String imageUrl,
        Double labor,
        String observation,
        String volt,
        String series,
        OrderStatus status,
        LocalDateTime lastReview,
        LocalDateTime analyzedAt,
        LocalDateTime completedAt
) {
}
