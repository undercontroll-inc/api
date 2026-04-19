package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.application.dto.*;
import com.undercontroll.application.mapper.OrderDtoMapper;
import com.undercontroll.domain.usecase.order.GetOrdersByUserIdPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrdersByUserIdImpl implements GetOrdersByUserIdPort {

    private final OrderGateway orderGateway;
    private final OrderDtoMapper orderMapper;

    @Override
    public Output execute(Input input) {
        List<Order> orders = orderGateway.findByUserId(input.userId());

        if (orders.isEmpty()) {
            return new Output(new GetOrdersByUserIdResponse(
                    new ArrayList<>()
            ));
        }

        List<OrderEnrichedDto> orderDtos = orders.stream()
                .map(orderMapper::toEnrichedDto)
                .toList();

        return new Output(new GetOrdersByUserIdResponse(orderDtos));
    }

}
