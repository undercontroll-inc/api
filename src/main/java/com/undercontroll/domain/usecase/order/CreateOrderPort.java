package com.undercontroll.domain.usecase.order;

import com.undercontroll.application.dto.order.CreateOrderRequest;
import com.undercontroll.application.dto.order.OrderEnrichedDto;

public interface CreateOrderPort {
    OrderEnrichedDto execute(CreateOrderRequest request);
}
