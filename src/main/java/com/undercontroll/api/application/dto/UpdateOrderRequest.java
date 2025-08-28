package com.undercontroll.api.application.dto;

import java.time.LocalDateTime;

public record UpdateOrderRequest(
        Integer id,
        LocalDateTime startedAt,
        LocalDateTime completedTime
) {
}
