package com.undercontroll.api.dto;

import java.time.LocalDateTime;

public record OrderItemDto(
    String name,
    String imageUrl,
    String observation,
    String volt,
    String series,
    LocalDateTime lastReview,
    LocalDateTime analyzedAt,
    LocalDateTime completedAt
) {
}
