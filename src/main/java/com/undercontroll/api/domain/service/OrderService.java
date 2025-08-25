package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.dto.OrderDto;
import com.undercontroll.api.application.dto.OrderItemDto;
import com.undercontroll.api.application.port.OrderPort;
import com.undercontroll.api.domain.model.Order;
import com.undercontroll.api.infrastructure.persistence.adapter.OrderItemPersistenceAdapter;
import com.undercontroll.api.infrastructure.persistence.adapter.OrderPersistenceAdapter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService implements OrderPort {

    private final OrderPersistenceAdapter adapter;
    private final OrderItemPersistenceAdapter orderItemAdapter;

    public OrderService(OrderPersistenceAdapter adapter, OrderItemPersistenceAdapter orderItemAdapter) {
        this.adapter = adapter;
        this.orderItemAdapter = orderItemAdapter;
    }

    @Override
    public Order createOrder() {
        // Validacao aqui

        Order order = new Order();

        Order orderSaved = adapter.saveOrder(order);

        adapter.saveOrder(orderSaved);

        return orderSaved;
    }

    @Override
    public void updateOrder(Order order) {

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

    }
}
