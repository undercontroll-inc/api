package com.undercontroll.domain.usecase.order_item.impl;

import com.undercontroll.domain.usecase.order_item.CreateOrderItemPort;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.exception.InvalidOrderItemException;
import com.undercontroll.domain.gateway.OrderItemGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateOrderItemImpl implements CreateOrderItemPort {

    private final OrderItemGateway orderItemGateway;

    @Override
    public Output execute(Input input) {
        validateCreateOrderItemRequest(input);
        OrderItem orderItem = OrderItem.builder()
                .brand(input.brand())
                .model(input.model())
                .type(input.type())
                .imageUrl(input.imageUrl())
                .observation(input.observation())
                .volt(input.volt())
                .series(input.series())
                .laborValue(input.laborValue())
                .build();

        OrderItem savedItem = orderItemGateway.save(orderItem);

        return new Output(
                savedItem.getId(),
                savedItem.getBrand(),
                savedItem.getModel(),
                savedItem.getType(),
                savedItem.getLaborValue()
        );
    }

    private void validateCreateOrderItemRequest(Input input) {
        if (input.laborValue() != null && input.laborValue() < 0) {
            throw new InvalidOrderItemException("Order item labor cannot be negative");
        }
    }
}
