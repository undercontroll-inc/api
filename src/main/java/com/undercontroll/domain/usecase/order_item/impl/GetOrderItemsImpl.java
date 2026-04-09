package com.undercontroll.domain.usecase.order_item.impl;

import com.undercontroll.domain.usecase.order_item.GetOrderItemsPort;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.gateway.OrderItemGateway;
import com.undercontroll.application.dto.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrderItemsImpl implements GetOrderItemsPort {

    private final OrderItemGateway orderItemGateway;

    @Override
    public Output execute(Input input) {
        List<OrderItemDto> orderItems = orderItemGateway.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
        return new Output(orderItems);
    }

    private OrderItemDto mapToDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
