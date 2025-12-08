package com.undercontroll.api.dto;

import com.undercontroll.api.model.enums.OrderStatus;

import java.util.List;

public record OrdersByStatusResponse(
    List<StatusCount> statusCounts
) {
    public record StatusCount(
        OrderStatus status,
        Long count
    ) {}
}

