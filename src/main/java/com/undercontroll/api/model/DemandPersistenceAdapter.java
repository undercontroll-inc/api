package com.undercontroll.api.model;

import com.undercontroll.api.model.Demand;
import com.undercontroll.api.repository.DemandRepository;
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
