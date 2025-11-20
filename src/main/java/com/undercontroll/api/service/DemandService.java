package com.undercontroll.api.service;

import com.undercontroll.api.dto.CreateDemandRequest;
import com.undercontroll.api.exception.InvalidDemandException;
import com.undercontroll.api.model.Demand;
import com.undercontroll.api.model.Order;
import com.undercontroll.api.repository.DemandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DemandService {

    private final DemandRepository repository;
    private final MetricsService metricsService;

    public Demand createDemand(CreateDemandRequest createDemandRequest) {
        if(createDemandRequest.quantity() == null || createDemandRequest.quantity() <= 0) {
            throw new InvalidDemandException("Quantity should be greater than 0 and not null");
        }

        Demand demand = Demand.builder()
                .quantity(createDemandRequest.quantity())
                .component(createDemandRequest.componentPart())
                .order(createDemandRequest.order())
                .build();

        log.info("Creating demand for component {} in order {} with quantity {}",
                createDemandRequest.componentPart().getId(),
                createDemandRequest.order().getId(),
                createDemandRequest.quantity());

        Demand savedDemand = repository.save(demand);

        metricsService.incrementDemandCreated();

        return savedDemand;
    }

    public Demand updateDemand(Demand demand) {
        log.info("Updating demand {} for component {} with new quantity {}",
                demand.getId(),
                demand.getComponent().getId(),
                demand.getQuantity());
        return repository.save(demand);
    }

    public List<Demand> findDemandsByOrder(Order order) {
        return repository.findByOrder(order);
    }

    public Optional<Demand> findDemandByOrderAndComponent(Order order, Integer componentId) {
        return repository.findByOrderAndComponent_Id(order, componentId);
    }

    public void deleteDemand(Demand demand) {
        log.info("Deleting demand {} for component {} in order {}",
                demand.getId(),
                demand.getComponent().getId(),
                demand.getOrder().getId());

        repository.delete(demand);

        metricsService.incrementDemandRemoved();
    }

    public void deleteAllDemandsForOrder(Order order) {
        log.info("Deleting all demands for order {}", order.getId());
        repository.deleteByOrder(order);
    }

}
