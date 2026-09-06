package com.undercontroll.domain.usecase.order;

import com.undercontroll.application.dto.order.GetAllOrdersResponse;

public interface GetOrdersPort {
    GetAllOrdersResponse execute(Integer userId, Integer page, Integer size);
}
