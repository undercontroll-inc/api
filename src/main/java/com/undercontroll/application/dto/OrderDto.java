package com.undercontroll.application.dto;

import com.undercontroll.domain.enums.OrderStatus;

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
