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
    public void execute(Integer demandId) {
        demandGateway.deleteById(demandId);
    }
}
