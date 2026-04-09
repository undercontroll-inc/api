package com.undercontroll.domain.usecase.demand.impl;

import com.undercontroll.domain.usecase.demand.DeleteAllDemandsByOrderPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.gateway.DemandGateway;
import com.undercontroll.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeleteAllDemandsByOrderImpl implements DeleteAllDemandsByOrderPort {

    private final DemandGateway demandGateway;
    private final OrderGateway orderGateway;

    @Override
    public Output execute(Input input) {
        try {
            Optional<Order> orderOpt = orderGateway.findById(input.orderId());
            if (orderOpt.isEmpty()) {
                return new Output(false, "Order not found");
            }
            Order order = orderOpt.get();
            demandGateway.deleteByOrder(order);
            return new Output(true, "Demands deleted successfully");
        } catch (Exception e) {
            return new Output(false, "Failed to delete demands: " + e.getMessage());
        }
    }
}
