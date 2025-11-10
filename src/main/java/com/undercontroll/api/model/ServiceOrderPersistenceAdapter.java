package com.undercontroll.api.model;

import com.undercontroll.api.model.ServiceOrder;
import com.undercontroll.api.repository.ServiceOrderJpaRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    public void deleteServiceOrder(ServiceOrder serviceOrder) {repository.delete(serviceOrder);}

    public Optional<ServiceOrder> findServiceOrderById(Integer orderId) {
        return this.repository.findById(orderId);
    }

    @Transactional
    public void updateServiceOrder(ServiceOrder serviceOrder) {
        repository.save(serviceOrder);
    }

    public List<ServiceOrder> findServiceOrdersByOrderId(@NotNull @Positive Integer orderId) {return repository.findByOrder_Id(orderId);}
}
