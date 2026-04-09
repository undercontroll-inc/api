package com.undercontroll.domain.usecase.order_item.impl;

import com.undercontroll.domain.usecase.order_item.GetOrderItemByIdPort;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.gateway.OrderItemGateway;
import com.undercontroll.application.dto.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetOrderItemByIdImpl implements GetOrderItemByIdPort {

    private final OrderItemGateway orderItemGateway;

    @Override
    public Output execute(Input input) {
        Optional<OrderItem> orderItem = orderItemGateway.findById(input.orderItemId());
        if (orderItem.isEmpty()) {
            return new Output(null);
        }
        OrderItem oi = orderItem.get();
        return new Output(new OrderItemDto(
                oi.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        ));
    }
}
