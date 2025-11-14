package com.undercontroll.api.service;

import com.undercontroll.api.dto.CreateDemandRequest;
import com.undercontroll.api.exception.InvalidDemandException;
import com.undercontroll.api.model.Demand;
import com.undercontroll.api.repository.DemandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DemandService {

    private final DemandRepository repository;

    public Demand createDemand(CreateDemandRequest createDemandRequest) {
        if(createDemandRequest.quantity() == null || createDemandRequest.quantity() <= 0) {
            throw new InvalidDemandException("Quantity should be greater than 0 and not null");
        }

        Demand demand = Demand.builder()
                .quantity(createDemandRequest.quantity())
                .component(createDemandRequest.componentPart())
                .order(createDemandRequest.order())
                .build();

        return repository.save(demand);
    }
}
