package com.undercontroll.api.dto;

import java.util.List;

public record GetOrdersByUserIdResponse(
        List<OrderEnrichedDto> data
) {
}
