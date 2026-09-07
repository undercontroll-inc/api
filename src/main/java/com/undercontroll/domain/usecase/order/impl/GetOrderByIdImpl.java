package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.application.mapper.OrderDtoMapper;
import com.undercontroll.domain.gateway.CurrentUserAdminPort;
import com.undercontroll.domain.usecase.order.GetOrderByIdPort;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.application.dto.order.GetOrderByIdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetOrderByIdImpl implements GetOrderByIdPort {

    private final OrderGateway orderGateway;
    private final OrderDtoMapper orderMapper;
    private final CurrentUserAdminPort currentUserAdminPort;

    @Override
    public Optional<GetOrderByIdResponse> execute(Integer orderId) {
        boolean includeTechnical = currentUserAdminPort.isAdministrator();
        return orderGateway.findById(orderId)
                .map(order -> orderMapper.toEnrichedDto(order, includeTechnical))
                .map(GetOrderByIdResponse::new);
    }
}
