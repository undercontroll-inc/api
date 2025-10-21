package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.dto.CreateDemandRequest;
import com.undercontroll.api.application.port.DemandPort;
import com.undercontroll.api.infrastructure.persistence.adapter.DemandPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DemandService implements DemandPort {

    private final DemandPersistenceAdapter adapter;

    @Override
    public void createDemand(CreateDemandRequest createDemandRequest) {
        
    }
}
