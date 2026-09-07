package com.undercontroll.application.dto.order;

import com.undercontroll.application.dto.orderitem.UpdateOrderItemDto;
import com.undercontroll.domain.enums.OrderStatus;

import java.util.List;

public record UpdateOrderRequest(
        OrderStatus status,
        List<UpdateOrderItemDto> appliances,
        List<PartDto> parts,
        String customerDescription,
        String technicalDescription
) {
}
