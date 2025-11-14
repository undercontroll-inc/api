package com.undercontroll.api.dto;

import com.undercontroll.api.model.OrderStatus;

import java.util.List;

public record OrderEnrichedDto(
        Integer id,
        Integer userId,
        List<OrderItemDto> appliances,
        List<ComponentDto> parts,
        Double partsTotal,
        Double laborTotal,
        Double discount,
        Double totalValue,
        String receivedAt,
        String deadline,
        String nf,
        boolean haveReturnGuarantee,
        String serviceDescription,
        String notes,
        OrderStatus status,
        String updatedAt
) {
}
