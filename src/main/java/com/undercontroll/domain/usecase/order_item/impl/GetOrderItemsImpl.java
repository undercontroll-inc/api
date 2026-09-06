package com.undercontroll.domain.usecase.order_item.impl;

import com.undercontroll.application.dto.orderitem.OrderItemDto;
import com.undercontroll.application.mapper.OrderItemDtoMapper;
import com.undercontroll.domain.usecase.order_item.GetOrderItemsPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.exception.OrderNotFoundException;
import com.undercontroll.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrderItemsImpl implements GetOrderItemsPort {

    private final OrderGateway orderGateway;
    private final OrderItemDtoMapper orderItemDtoMapper;

    @Override
    public List<OrderItemDto> execute(Integer orderId) {
        Order order = orderGateway.findDetailById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        return order.getOrderItems() == null
                ? List.of()
                : order.getOrderItems().stream().map(orderItemDtoMapper::toDto).toList();
    }
}
