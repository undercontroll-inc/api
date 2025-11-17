package com.undercontroll.api.dto;

import com.undercontroll.api.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        List<OrderItemDto> orderItems,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime completedTime,
        OrderStatus status
) {
}
