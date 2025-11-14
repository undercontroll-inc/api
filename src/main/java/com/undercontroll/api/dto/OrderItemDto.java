package com.undercontroll.api.dto;

import java.time.LocalDateTime;

public record OrderItemDto(
    String imageUrl,
    String model,
    String type,
    String brand,
    String observation,
    String volt,
    String series,
    LocalDateTime lastReview,
    LocalDateTime analyzedAt,
    LocalDateTime completedAt
) {
}
