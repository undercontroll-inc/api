package com.undercontroll.domain.usecase.demand.impl;

import com.undercontroll.application.dto.demand.DemandDto;
import com.undercontroll.domain.usecase.demand.GetDemandsPort;
import com.undercontroll.domain.model.Demand;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.exception.InvalidDemandException;
import com.undercontroll.domain.gateway.DemandGateway;
import com.undercontroll.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDemandsImpl implements GetDemandsPort {

    private final DemandGateway demandGateway;
    private final OrderGateway orderGateway;

    @Override
    public List<DemandDto> execute(Integer orderId, Integer componentId) {
        Order order = orderGateway.findById(orderId)
                .orElseThrow(() -> new InvalidDemandException("Order not found with id: " + orderId));

        List<Demand> demands = componentId == null
                ? demandGateway.findByOrder(order)
                : demandGateway.findByOrderAndComponentId(order, componentId).stream().toList();

        return demands.stream()
                .map(demand -> new DemandDto(
                        demand.getId(),
                        demand.getComponent().getId(),
                        demand.getOrder().getId(),
                        demand.getQuantity()
                ))
                .toList();
    }
}
