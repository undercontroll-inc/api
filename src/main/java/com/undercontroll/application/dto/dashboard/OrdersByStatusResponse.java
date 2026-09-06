package com.undercontroll.application.dto.dashboard;

import com.undercontroll.domain.enums.OrderStatus;

import java.util.List;

public record OrdersByStatusResponse(
        List<StatusCount> statusCounts
) {
    public record StatusCount(
            OrderStatus status,
            Long count
    ) {
    }
}
