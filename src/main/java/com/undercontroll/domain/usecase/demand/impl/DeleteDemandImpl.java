package com.undercontroll.domain.usecase.demand.impl;

import com.undercontroll.domain.usecase.demand.DeleteDemandPort;
import com.undercontroll.domain.gateway.DemandGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteDemandImpl implements DeleteDemandPort {

    private final DemandGateway demandGateway;

    @Override
    public Output execute(Input input) {
        try {
            demandGateway.deleteById(input.demandId());
            return new Output(true, "Demand deleted successfully");
        } catch (Exception e) {
            return new Output(false, "Failed to delete demand: " + e.getMessage());
        }
    }
}
