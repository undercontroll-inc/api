package com.undercontroll.domain.usecase.order_item.impl;

import com.undercontroll.application.dto.orderitem.CreateOrderItemRequest;
import com.undercontroll.application.dto.orderitem.OrderItemDto;
import com.undercontroll.application.mapper.OrderItemDtoMapper;
import com.undercontroll.domain.usecase.order_item.CreateOrderItemPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.exception.InvalidOrderItemException;
import com.undercontroll.domain.exception.OrderNotFoundException;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.gateway.OrderItemGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderItemImpl implements CreateOrderItemPort {

    private final OrderItemGateway orderItemGateway;
    private final OrderGateway orderGateway;
    private final OrderItemDtoMapper orderItemDtoMapper;

    @Override
    public OrderItemDto execute(CreateOrderItemRequest request) {
        validateCreateOrderItemRequest(request);
        OrderItem savedItem = orderItemGateway.save(toDomain(request));
        log.info("Order item created id={}", savedItem.getId());
        return orderItemDtoMapper.toDto(savedItem);
    }

    @Override
    public OrderItemDto execute(Integer orderId, CreateOrderItemRequest request) {
        validateCreateOrderItemRequest(request);

        Order order = orderGateway.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        OrderItem savedItem = orderItemGateway.save(toDomain(request));
        order.addOrderItem(savedItem);
        orderGateway.save(order);
        log.info("Order item created orderId={} itemId={}", orderId, savedItem.getId());
        return orderItemDtoMapper.toDto(savedItem);
    }

    private OrderItem toDomain(CreateOrderItemRequest request) {
        return OrderItem.builder()
                .brand(request.brand())
                .model(request.model())
                .type(request.type())
                .imageUrl(request.imageUrl())
                .volt(request.volt())
                .series(request.series())
                .laborValue(request.laborValue())
                .build();
    }

    private void validateCreateOrderItemRequest(CreateOrderItemRequest request) {
        if (request.laborValue() != null && request.laborValue() < 0) {
            throw new InvalidOrderItemException("Order item labor cannot be negative");
        }
    }
}
