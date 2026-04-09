package com.undercontroll.application.dto;

import java.util.List;

public record GetAllOrdersResponse(
        List<OrderEnrichedDto> data
) {
}
