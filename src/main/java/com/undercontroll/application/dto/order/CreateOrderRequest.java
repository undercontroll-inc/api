package com.undercontroll.application.dto.order;

import com.undercontroll.application.dto.orderitem.OrderItemCreateOrderRequest;

import java.util.List;

public record CreateOrderRequest(

        Integer userId,
        List<OrderItemCreateOrderRequest> appliances,
        List<PartDto> parts,
        Double discount,
        String receivedAt,
        String deadline,
        String customerDescription,
        String technicalDescription,
        String status,
        boolean returnGuarantee,
        boolean fabricGuarantee,
        String nf

) {
}
