package com.undercontroll.application.dto;

import com.undercontroll.domain.enums.OrderStatus;

import java.util.List;

public record UpdateOrderRequest(
        OrderStatus status,
        List<UpdateOrderItemDto> appliances,
        List<PartDto> parts,
        String serviceDescription
) {
}
