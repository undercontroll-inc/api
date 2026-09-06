package com.undercontroll.domain.usecase.order_item;

import com.undercontroll.application.dto.orderitem.OrderItemDto;

import java.util.Optional;

public interface GetOrderItemByIdPort {
    Optional<OrderItemDto> execute(Integer orderId, Integer orderItemId);
}
