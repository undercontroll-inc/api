package com.undercontroll.domain.usecase.order_item;

import com.undercontroll.application.dto.orderitem.OrderItemDto;

import java.util.List;

public interface GetOrderItemsPort {
    List<OrderItemDto> execute(Integer orderId);
}
