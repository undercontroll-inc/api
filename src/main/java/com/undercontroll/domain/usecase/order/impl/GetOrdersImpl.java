package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.application.mapper.OrderDtoMapper;
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
    @Cacheable(value = "orders")
    public Output execute(Input input) {
        log.info("Fetching all orders");

        List<Order> orders = orderGateway.findAll();

        List<OrderEnrichedDto> enrichedOrders = orders.stream()
                .map(orderMapper::toEnrichedDto)
                .toList();

        return new Output(enrichedOrders);
    }
}
