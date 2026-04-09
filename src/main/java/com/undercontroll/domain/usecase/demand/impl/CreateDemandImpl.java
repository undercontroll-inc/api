package com.undercontroll.domain.usecase.demand.impl;

import com.undercontroll.domain.usecase.demand.CreateDemandPort;
import com.undercontroll.domain.model.ComponentPart;
import com.undercontroll.domain.model.Demand;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.exception.InvalidDemandException;
import com.undercontroll.domain.gateway.ComponentGateway;
import com.undercontroll.domain.gateway.DemandGateway;
import com.undercontroll.infrastructure.service.MetricsService;
import com.undercontroll.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDemandImpl implements CreateDemandPort {

    private final DemandGateway demandGateway;
    private final ComponentGateway componentGateway;
    private final OrderGateway orderGateway;
    private final MetricsService metricsService;

    @Override
    public Output execute(Input input) {
        if (input.quantity() == null || input.quantity() <= 0) {
            throw new InvalidDemandException("Quantity should be greater than 0 and not null");
        }

        ComponentPart component = componentGateway.findById(input.componentPartId())
                .orElseThrow(() -> new InvalidDemandException("Component not found"));
        Order order = orderGateway.findById(input.orderId())
                .orElseThrow(() -> new InvalidDemandException("Order not found"));

        Demand demand = Demand.builder()
                .quantity(input.quantity())
                .component(component)
                .order(order)
                .build();

        log.info("Creating demand for component {} in order {} with quantity {}",
                input.componentPartId(),
                input.orderId(),
                input.quantity());

        Demand savedDemand = demandGateway.save(demand);

        metricsService.incrementDemandCreated();

        return new Output(
                savedDemand.getId(),
                savedDemand.getComponent().getId(),
                savedDemand.getOrder().getId(),
                savedDemand.getQuantity()
        );
    }
}
