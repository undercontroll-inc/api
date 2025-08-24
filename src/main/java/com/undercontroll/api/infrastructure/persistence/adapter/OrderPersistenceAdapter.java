package com.undercontroll.api.infrastructure.persistence.adapter;

import com.undercontroll.api.domain.exceptions.OrderNotFoundException;
import com.undercontroll.api.domain.model.Order;
import com.undercontroll.api.infrastructure.persistence.repository.OrderJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderPersistenceAdapter {

    private final OrderJpaRepository repository;

    public OrderPersistenceAdapter(OrderJpaRepository repository) {
        this.repository = repository;
    }

    public Order saveOrder(Order order) {
        return repository.save(order);
    }

    public List<Order> getOrders() {
        return this.repository.findAll();
    }

    public void deleteOrder(Integer orderId) {
        Optional<Order> orderFound = repository.findById(orderId);

        if(orderFound.isEmpty()) {
            throw new OrderNotFoundException("Order not found for deletion");
        };

        repository.delete(orderFound.get());
    }

    @Transactional
    public void updateOrder(Order order) {
        repository.save(order);
    }

}
