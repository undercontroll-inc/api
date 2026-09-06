package com.undercontroll.domain.usecase.order;

import com.undercontroll.application.dto.order.GetOrderByIdResponse;

import java.util.Optional;

public interface GetOrderByIdPort {
    Optional<GetOrderByIdResponse> execute(Integer orderId);
}
