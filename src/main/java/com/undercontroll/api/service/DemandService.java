package com.undercontroll.api.service;

import com.undercontroll.api.dto.CreateDemandRequest;
import com.undercontroll.api.model.Demand;
import com.undercontroll.api.repository.DemandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DemandService {

    private final DemandRepository repository;

    public Demand createDemand(CreateDemandRequest createDemandRequest) {
        return null;
    }
}
