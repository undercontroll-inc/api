package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.application.mapper.OrderDtoMapper;
import com.undercontroll.domain.model.PaginatedResult;
import com.undercontroll.domain.usecase.order.GetOrdersPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.application.dto.OrderEnrichedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetOrdersImpl implements GetOrdersPort {

    private final OrderGateway orderGateway;
    private final OrderDtoMapper orderMapper;

    @Override
    @Cacheable(value = "orders", key = "#input.offset() + '-' + #input.limit()")
    public Output execute(Input input) {
        log.info("Fetching all orders");

        PaginatedResult<Order> result = orderGateway.findAllPaginated(input.offset(), input.limit());

        List<OrderEnrichedDto> enrichedOrders = result.content().stream()
                .map(orderMapper::toEnrichedDto)
                .toList();

        int totalPages = input.limit() > 0
                ? (int) Math.ceil((double) result.totalElements() / input.limit())
                : 0;

        return new Output(enrichedOrders, result.totalElements(), totalPages);
    }
}
