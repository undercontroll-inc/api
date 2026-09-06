package com.undercontroll.application.dto.order;

import java.util.List;

public record GetAllOrdersResponse(
        List<OrderEnrichedDto> data,
        long totalElements,
        int totalPages,
        int page,
        int size
) {}
