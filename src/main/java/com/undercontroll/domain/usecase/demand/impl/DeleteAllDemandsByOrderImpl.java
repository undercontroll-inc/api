package com.undercontroll.domain.usecase.demand.impl;

import com.undercontroll.domain.usecase.demand.DeleteAllDemandsByOrderPort;
import com.undercontroll.domain.exception.OrderNotFoundException;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.gateway.DemandGateway;
import com.undercontroll.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteAllDemandsByOrderImpl implements DeleteAllDemandsByOrderPort {

    private final DemandGateway demandGateway;
    private final OrderGateway orderGateway;

    @Override
    public void execute(Integer orderId) {
        Order order = orderGateway.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        demandGateway.deleteByOrder(order);
    }
}
