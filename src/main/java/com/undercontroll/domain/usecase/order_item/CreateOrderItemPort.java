package com.undercontroll.domain.usecase.order_item;

import com.undercontroll.application.dto.orderitem.CreateOrderItemRequest;
import com.undercontroll.application.dto.orderitem.OrderItemDto;

public interface CreateOrderItemPort {
    OrderItemDto execute(CreateOrderItemRequest request);

    OrderItemDto execute(Integer orderId, CreateOrderItemRequest request);
}
