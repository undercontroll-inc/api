package com.undercontroll.api.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        List<OrderItemDto> orderItems,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime completedTime
) {
}
