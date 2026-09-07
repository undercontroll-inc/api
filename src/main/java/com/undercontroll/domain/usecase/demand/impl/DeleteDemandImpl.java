package com.undercontroll.domain.usecase.demand.impl;

import com.undercontroll.domain.usecase.demand.DeleteDemandPort;
import com.undercontroll.domain.gateway.DemandGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteDemandImpl implements DeleteDemandPort {

    private final DemandGateway demandGateway;

    @Override
    public void execute(Integer demandId) {
        demandGateway.deleteById(demandId);
        log.info("Demand deleted demandId={}", demandId);
    }
}
