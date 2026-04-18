package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.application.mapper.OrderDtoMapper;
import com.undercontroll.domain.usecase.order.GetOrderByIdPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.application.dto.GetOrderByIdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetOrderByIdImpl implements GetOrderByIdPort {

    private final OrderGateway orderGateway;
    private final OrderDtoMapper orderMapper;

    @Override
    public Output execute(Input input) {
        Optional<Order> order = orderGateway.findById(input.orderId());
        if (order.isEmpty()) {
            return new Output(null);
        }
        Order o = order.get();

        final var orderDto = orderMapper.toEnrichedDto(o);

        return new Output(new GetOrderByIdResponse(orderDto));
    }
}
