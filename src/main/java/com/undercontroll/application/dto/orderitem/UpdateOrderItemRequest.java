package com.undercontroll.application.dto.orderitem;

import java.time.LocalDateTime;

public record UpdateOrderItemRequest(
        String imageUrl,
        Double labor,
        String volt,
        String series,
        String type,
        String brand,
        String model,
        LocalDateTime completedAt
) {
}
