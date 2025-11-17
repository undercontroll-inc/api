package com.undercontroll.api.dto;

import java.time.LocalDateTime;

public record UpdateOrderItemRequest(
        Integer id,
        String imageUrl,
        Double labor,
        String observation,
        String volt,
        String series,
        String type,
        String brand,
        String model,
        LocalDateTime completedAt
) {
}
