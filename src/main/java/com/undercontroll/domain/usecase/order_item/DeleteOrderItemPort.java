package com.undercontroll.domain.usecase.order_item;

public interface DeleteOrderItemPort {
    void execute(Integer orderId, Integer orderItemId);
}
