package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.enums.OrderItemStatus;

import java.time.LocalDateTime;

public record OrderItemDto(
    String name,
    String imageUrl,
    Double price,
    Double discount,
    Integer quantity,
    OrderItemStatus status,
    LocalDateTime sentAt,
    LocalDateTime requestedAt,
    LocalDateTime lastReview,
    LocalDateTime analyzedAt,
    LocalDateTime completedAt,
    LocalDateTime payedAt

) {
}
