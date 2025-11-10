package com.undercontroll.api.service;

import com.undercontroll.api.dto.CreateDemandRequest;
import com.undercontroll.api.model.DemandPort;
import com.undercontroll.api.model.DemandPersistenceAdapter;
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
