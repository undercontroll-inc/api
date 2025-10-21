package com.undercontroll.api.infrastructure.persistence.adapter;

import com.undercontroll.api.domain.model.Demand;
import com.undercontroll.api.infrastructure.persistence.repository.DemandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DemandPersistenceAdapter {

    private final DemandRepository repository;

    public void createDemand(Demand demand) {
        repository.save(demand);
    }

}
