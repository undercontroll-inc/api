package com.undercontroll.api.service;

import com.undercontroll.api.dto.CreateOrderRequest;
import com.undercontroll.api.dto.OrderDto;
import com.undercontroll.api.dto.OrderItemDto;
import com.undercontroll.api.dto.UpdateOrderRequest;
import com.undercontroll.api.model.OrderPort;
import com.undercontroll.api.exception.InvalidDeleteOrderException;
import com.undercontroll.api.exception.InvalidUpdateOrderException;
import com.undercontroll.api.exception.OrderNotFoundException;
import com.undercontroll.api.model.Order;
import com.undercontroll.api.model.OrderItem;
import com.undercontroll.api.model.OrderItemPersistenceAdapter;
import com.undercontroll.api.model.OrderPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderPort {

    private final OrderPersistenceAdapter adapter;
    private final OrderItemPersistenceAdapter orderItemAdapter;

    @Override
    public Order createOrder(
            CreateOrderRequest request
    ) {
        List<OrderItem> orderItems = new ArrayList<>();

        if(request.orderItemIds() != null && !request.orderItemIds().isEmpty()) {
            orderItems = orderItemAdapter.findAllById(request.orderItemIds());
        }

        Order order = Order.builder()
                .total(0.0)
                .discount(0.0)
                .orderItems(orderItems)
                .build();

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
                                .map(i -> new OrderItemDto(
                                        i.getName(),
                                        i.getImageUrl(),
                                        i.getLabor(),
                                        i.getObservation(),
                                        i.getVolt(),
                                        i.getSeries(),
                                        i.getStatus(),
                                        i.getLastReview(),
                                        i.getAnalyzedAt(),
                                        i.getCompletedAt()
                                ))
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
