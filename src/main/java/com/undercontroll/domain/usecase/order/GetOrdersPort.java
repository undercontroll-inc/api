package com.undercontroll.domain.usecase.order;

import com.undercontroll.application.dto.OrderEnrichedDto;

import java.util.List;

public interface GetOrdersPort {
    record Input(
            Integer offset,
            Integer limit
    ) {}

    record Output(
            List<OrderEnrichedDto> orders,
            long totalElements,
            int totalPages
    ) {}

    Output execute(Input input);
}
