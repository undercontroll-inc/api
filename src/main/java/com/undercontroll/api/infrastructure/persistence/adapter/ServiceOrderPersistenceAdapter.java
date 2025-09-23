package com.undercontroll.api.infrastructure.persistence.adapter;

import com.undercontroll.api.domain.model.ServiceOrder;
import com.undercontroll.api.infrastructure.persistence.repository.ServiceOrderJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ServiceOrderPersistenceAdapter {

    private final ServiceOrderJpaRepository repository;

    public ServiceOrder saveServiceOrder(ServiceOrder serviceOrder) {return repository.save(serviceOrder);}

    public List<ServiceOrder> getServiceOrders() {
        return this.repository.findAll();
    }

    public void deleteOrder(ServiceOrder serviceOrder) {repository.delete(serviceOrder);}

    public Optional<ServiceOrder> findServiceOrderById(Integer orderId) {
        return this.repository.findById(orderId);
    }

    @Transactional
    public void updateServiceOrder(ServiceOrder serviceOrder) {
        repository.save(serviceOrder);
    }

}
