package com.undercontroll.domain.usecase.order_item.impl;

import com.undercontroll.domain.usecase.order_item.DeleteOrderItemPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.exception.InvalidOrderItemException;
import com.undercontroll.domain.exception.OrderItemNotFoundException;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.gateway.OrderItemGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteOrderItemImpl implements DeleteOrderItemPort {

    private final OrderItemGateway orderItemGateway;
    private final OrderGateway orderGateway;

    @Override
    public void execute(Integer orderId, Integer orderItemId) {
        log.info("Deleting order item {} from order {}", orderItemId, orderId);

        if (orderItemId == null || orderItemId <= 0) {
            throw new InvalidOrderItemException("Invalid order item ID");
        }

        Order order = orderGateway.findOrderByOrderItemId(orderItemId)
                .orElseThrow(() -> new OrderItemNotFoundException(
                        "Could not find the order item with id: %s".formatted(orderItemId)
                ));
        if (!orderId.equals(order.getId())) {
            throw new OrderItemNotFoundException(
                    "Order item %s does not belong to order %s".formatted(orderItemId, orderId)
            );
        }

        orderItemGateway.deleteById(orderItemId);
    }
}
