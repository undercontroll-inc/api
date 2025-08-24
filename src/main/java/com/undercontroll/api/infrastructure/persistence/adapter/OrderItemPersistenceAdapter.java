package com.undercontroll.api.infrastructure.persistence.adapter;

import com.undercontroll.api.domain.exceptions.OrderItemNotFoundException;
import com.undercontroll.api.domain.model.OrderItem;
import com.undercontroll.api.infrastructure.persistence.repository.OrderItemJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderItemPersistenceAdapter {

    private final OrderItemJpaRepository repository;

    public OrderItemPersistenceAdapter(OrderItemJpaRepository repository) {
        this.repository = repository;
    }

    public OrderItem saveOrderItem(OrderItem orderItem) {
        return repository.save(orderItem);
    }

    public List<OrderItem> getOrderItems() {
        return repository.findAll();
    }

    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return repository.findByOrderId(orderId);
    }

    public OrderItem getOrderItemById(Integer orderItemId) {
        return repository.findById(orderItemId)
                .orElseThrow(() -> new OrderItemNotFoundException("Order item not found with ID: " + orderItemId));
    }

    public void deleteOrderItem(Integer orderItemId) {
        Optional<OrderItem> orderItemFound = repository.findById(orderItemId);

        if (orderItemFound.isEmpty()) {
            throw new OrderItemNotFoundException("Order item not found for deletion");
        }

        repository.delete(orderItemFound.get());
    }

    @Transactional
    public void updateOrderItem(OrderItem orderItem) {
        repository.save(orderItem);
    }
}