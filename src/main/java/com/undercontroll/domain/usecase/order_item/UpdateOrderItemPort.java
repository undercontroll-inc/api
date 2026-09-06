package com.undercontroll.domain.usecase.order_item;

import com.undercontroll.application.dto.orderitem.UpdateOrderItemRequest;

public interface UpdateOrderItemPort {
    void execute(Integer orderId, Integer orderItemId, UpdateOrderItemRequest request);
}
