package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.dto.OrderDto;
import com.undercontroll.api.application.dto.OrderItemDto;
import com.undercontroll.api.application.dto.UpdateOrderRequest;
import com.undercontroll.api.application.port.OrderPort;
import com.undercontroll.api.domain.exceptions.InvalidDeleteOrderException;
import com.undercontroll.api.domain.exceptions.InvalidUpdateOrderException;
import com.undercontroll.api.domain.exceptions.OrderNotFoundException;
import com.undercontroll.api.domain.model.Order;
import com.undercontroll.api.infrastructure.persistence.adapter.OrderPersistenceAdapter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements OrderPort {

    private final OrderPersistenceAdapter adapter;


    public OrderService(OrderPersistenceAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public Order createOrder() {

        Order order = new Order();

        Order orderSaved = adapter.saveOrder(order);

        adapter.saveOrder(orderSaved);

        return orderSaved;
    }

    @Override
    public void updateOrder(UpdateOrderRequest request) {
        validateUpdateOrder(request);

        Optional<Order> orderFound = adapter.findOrderById(request.id());

        if(orderFound.isEmpty()) {
            throw new OrderNotFoundException("Could not found the order");
        }

        if(request.completedTime() != null) {
            orderFound.get().setCompletedTime(request.completedTime());
        }

        if(request.startedAt() != null) {
            orderFound.get().setStartedAt(request.startedAt());
        }

        adapter.updateOrder(orderFound.get());
    }

    @Override
    public List<OrderDto> getOrders() {
        return adapter
                .getOrders()
                .stream()
                .map(o -> new OrderDto(
                        o.getOrderItems()
                                .stream()
                                .map(i -> {
                                    return new OrderItemDto(
                                            i.getName(),
                                            i.getImageUrl(),
                                            i.getPrice(),
                                            i.getDiscount(),
                                            i.getQuantity(),
                                            i.getStatus(),
                                            i.getSentAt(),
                                            i.getRequestedAt(),
                                            i.getLastReview(),
                                            i.getAnalyzedAt(),
                                            i.getCompletedAt(),
                                            i.getPayedAt()
                                    );
                                })
                                .toList(),
                        o.getCreatedAt(),
                        o.getStartedAt(),
                        o.getCompletedTime()
                ))
                .toList();
    }

    @Override
    public void deleteOrder(Integer orderId) {
        validateDeleteOrder(orderId);

        Optional<Order> orderFound = adapter.findOrderById(orderId);

        if(orderFound.isEmpty()) {
            throw new OrderNotFoundException("Could not found the order");
        }

        adapter.deleteOrder(orderFound.get());
    }

    private void validateUpdateOrder(UpdateOrderRequest request) {
        if(request.id() == null || request.id() <= 0){
            throw new InvalidUpdateOrderException("Order id cannot be null for the update");
        }
    }

    private void validateDeleteOrder(Integer orderId) {
        if(orderId == null || orderId <= 0){
            throw new InvalidDeleteOrderException("Order id cannot be null for the delete");
        }
    }

}
