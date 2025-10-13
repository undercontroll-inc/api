package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.enums.OrderItemStatus;

import java.time.LocalDateTime;

public record UpdateOrderItemRequest(
        Integer id,
        String name,
        String imageUrl,
        Double labor,
        String observation,
        String volt,
        String series,
        OrderItemStatus status,
        LocalDateTime lastReview,
        LocalDateTime analyzedAt,
        LocalDateTime completedAt
) {
}
