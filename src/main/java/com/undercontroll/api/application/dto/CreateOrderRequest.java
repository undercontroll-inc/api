package com.undercontroll.api.application.dto;

import com.undercontroll.api.domain.model.OrderItem;

import java.util.List;

public record CreateOrderRequest(
        List<OrderItem> items
) {
}
