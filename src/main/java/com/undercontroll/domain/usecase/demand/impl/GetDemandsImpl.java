package com.undercontroll.domain.usecase.demand.impl;

import com.undercontroll.domain.usecase.demand.GetDemandsPort;
import com.undercontroll.domain.model.Demand;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.exception.InvalidDemandException;
import com.undercontroll.domain.gateway.DemandGateway;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.application.dto.DemandDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDemandsImpl implements GetDemandsPort {

    private final DemandGateway demandGateway;
    private final OrderGateway orderGateway;

    @Override
    public Output execute(Input input) {
        Order order = orderGateway.findById(input.orderId())
                .orElseThrow(() -> new InvalidDemandException("Order not found with id: " + input.orderId()));

        List<DemandDto> demands = demandGateway.findByOrder(order).stream()
                .map(this::mapToDto)
                .toList();

        return new Output(demands);
    }

    private DemandDto mapToDto(Demand demand) {
        return new DemandDto(
                demand.getId(),
                demand.getComponent().getId(),
                demand.getOrder().getId(),
                demand.getQuantity()
        );
    }
}
