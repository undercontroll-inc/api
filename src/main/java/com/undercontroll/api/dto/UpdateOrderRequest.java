package com.undercontroll.api.dto;

import com.undercontroll.api.model.enums.OrderStatus;

import java.util.List;

public record UpdateOrderRequest(
        OrderStatus status,
        List<UpdateOrderItemDto> appliances,
        List<PartDto> parts,
        String serviceDescription
) {
}
