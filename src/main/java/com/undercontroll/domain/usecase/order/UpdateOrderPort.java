package com.undercontroll.domain.usecase.order;

import com.undercontroll.application.dto.order.UpdateOrderRequest;

public interface UpdateOrderPort {
    void execute(Integer orderId, UpdateOrderRequest request);
}
