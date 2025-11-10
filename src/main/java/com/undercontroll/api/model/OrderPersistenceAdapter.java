package com.undercontroll.api.model;

import com.undercontroll.api.model.Order;
import com.undercontroll.api.repository.OrderJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter {

    private final OrderJpaRepository repository;

    public Order saveOrder(Order order) {
        return repository.save(order);
    }

    public List<Order> getOrders() {
        return this.repository.findAll();
    }

    public void deleteOrder(Order order) {
        repository.delete(order);
    }

    public Optional<Order> findOrderById(Integer orderId) {
        return this.repository.findById(orderId);
    }

    @Transactional
    public void updateOrder(Order order) {
        repository.save(order);
    }

}
