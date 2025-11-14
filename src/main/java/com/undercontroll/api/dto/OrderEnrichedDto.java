package com.undercontroll.api.dto;

import com.undercontroll.api.model.OrderStatus;

import java.util.List;

public record OrderEnrichedDto(
        Integer id,
        Integer userId,
        List<OrderItemDto> appliances,
        Double partsTotal,
        Double laborTotal,
        Double discount,
        Double totalValue,
        String receivedAt,
        String deadline,
        boolean haveReturnGuarantee,
        String serviceDescription,
        String notes,
        OrderStatus status,
        String updatedAt
) {
}
