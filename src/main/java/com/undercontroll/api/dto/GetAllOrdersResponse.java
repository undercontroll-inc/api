package com.undercontroll.api.dto;

import java.util.List;

public record GetAllOrdersResponse(
        List<OrderEnrichedDto> data
) {
}
