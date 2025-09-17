package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.dto.CreateOrderItemRequest;
import com.undercontroll.api.application.dto.OrderItemDto;
import com.undercontroll.api.application.dto.UpdateOrderItemRequest;
import com.undercontroll.api.application.port.OrderItemPort;
import com.undercontroll.api.domain.exceptions.InvalidOrderItemException;
import com.undercontroll.api.domain.exceptions.OrderNotFoundException;
import com.undercontroll.api.domain.model.Order;
import com.undercontroll.api.domain.model.OrderItem;
import com.undercontroll.api.infrastructure.persistence.adapter.OrderItemPersistenceAdapter;
import com.undercontroll.api.infrastructure.persistence.adapter.OrderPersistenceAdapter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService implements OrderItemPort {

    private final OrderItemPersistenceAdapter orderItemAdapter;
    private final OrderPersistenceAdapter orderAdapter;

    public OrderItemService(OrderItemPersistenceAdapter orderItemAdapter, OrderPersistenceAdapter orderAdapter) {
        this.orderItemAdapter = orderItemAdapter;
        this.orderAdapter = orderAdapter;
    }

    @Override
    public OrderItemDto createOrderItem(CreateOrderItemRequest request) {
        validateCreateOrderItemRequest(request);

        Optional<Order> orderOpt = orderAdapter.getOrders()
                .stream()
                .filter(order -> order.getId().equals(request.orderId()))
                .findFirst();

        if (orderOpt.isEmpty()) {
            throw new OrderNotFoundException("Order not found with ID: " + request.orderId());
        }

        Order order = orderOpt.get();

        OrderItem orderItem = new OrderItem(
                request.name(),
                request.imageUrl(),
                order,
                request.price(),
                request.discount() != null ? request.discount() : 0.0,
                request.quantity(),
                request.status(),
                null,
                LocalDateTime.now(),
                null,
                null,
                null,
                null
        );

        OrderItem orderItemSaved = orderItemAdapter.saveOrderItem(orderItem);

        order.addOrderItem(orderItemSaved);

        return new OrderItemDto(
                orderItemSaved.getName(),
                orderItemSaved.getImageUrl(),
                orderItemSaved.getPrice(),
                orderItemSaved.getDiscount(),
                orderItemSaved.getQuantity(),
                orderItemSaved.getStatus(),
                orderItemSaved.getSentAt(),
                orderItemSaved.getRequestedAt(),
                orderItemSaved.getLastReview(),
                orderItemSaved.getAnalyzedAt(),
                orderItemSaved.getCompletedAt(),
                orderItemSaved.getPayedAt()
        );
    }

    @Override
    public void updateOrderItem(UpdateOrderItemRequest data) {
        validateUpdateOrderItem(data);

        OrderItem orderItem = orderItemAdapter.getOrderItemById(data.id());

        if (data.name() != null) {
            orderItem.setName(data.name());
        }
        if (data.imageUrl() != null) {
            orderItem.setImageUrl(data.imageUrl());
        }
        if (data.price() != null) {
            orderItem.setPrice(data.price());
        }
        if (data.discount() != null) {
            orderItem.setDiscount(data.discount());
        }
        if (data.quantity() != null) {
            orderItem.setQuantity(data.quantity());
        }
        if (data.status() != null) {
            orderItem.setStatus(data.status());
        }
        if (data.sentAt() != null) {
            orderItem.setSentAt(data.sentAt());
        }
        if (data.requestedAt() != null) {
            orderItem.setRequestedAt(data.requestedAt());
        }
        if (data.lastReview() != null) {
            orderItem.setLastReview(data.lastReview());
        }
        if (data.analyzedAt() != null) {
            orderItem.setAnalyzedAt(data.analyzedAt());
        }
        if (data.completedAt() != null) {
            orderItem.setCompletedAt(data.completedAt());
        }
        if (data.payedAt() != null) {
            orderItem.setPayedAt(data.payedAt());
        }

        orderItemAdapter.updateOrderItem(orderItem);
    }

    @Override
    public List<OrderItemDto> getOrderItems() {
        return orderItemAdapter
                .getOrderItems()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<OrderItemDto> getOrderItemsByOrderId(Integer orderId) {
        if (orderId == null) {
            throw new InvalidOrderItemException("Order ID cannot be null");
        }

        return orderItemAdapter
                .getOrderItemsByOrderId(orderId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public void deleteOrderItem(Integer orderItemId) {
        if (orderItemId == null) {
            throw new InvalidOrderItemException("Order item ID cannot be null");
        }

        orderItemAdapter.deleteOrderItem(orderItemId);
    }

    @Override
    public OrderItemDto getOrderItemById(Integer orderItemId) {
        if (orderItemId == null) {
            throw new InvalidOrderItemException("Order item ID cannot be null");
        }

        OrderItem orderItem = orderItemAdapter.getOrderItemById(orderItemId);
        return mapToDto(orderItem);
    }

    private void validateCreateOrderItemRequest(CreateOrderItemRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new InvalidOrderItemException("Order item name cannot be empty");
        }

        if (request.price() == null || request.price() <= 0) {
            throw new InvalidOrderItemException("Order item price must be positive");
        }

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new InvalidOrderItemException("Order item quantity must be positive");
        }

        if (request.discount() == null || request.discount() <= 0) {
            throw new InvalidOrderItemException("Order item quantity must be positive");
        }

        if (request.orderId() == null || request.orderId() <= 0) {
            throw new InvalidOrderItemException("Order id must be valid");
        }

    }

    private void validateUpdateOrderItem(UpdateOrderItemRequest orderItem) {
        if (orderItem.id() == null) {
            throw new InvalidOrderItemException("Order item ID cannot be null for update");
        }

        if (orderItem.name().trim().isEmpty()) {
            throw new InvalidOrderItemException("Order item name cannot be empty");
        }

        if (orderItem.price() != null && orderItem.price() <= 0) {
            throw new InvalidOrderItemException("Order item price must be positive");
        }

        if (orderItem.quantity() != null && orderItem.quantity() <= 0) {
            throw new InvalidOrderItemException("Order item quantity must be positive");
        }
    }

    private OrderItemDto mapToDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getName(),
                orderItem.getImageUrl(),
                orderItem.getPrice(),
                orderItem.getDiscount(),
                orderItem.getQuantity(),
                orderItem.getStatus(),
                orderItem.getSentAt(),
                orderItem.getRequestedAt(),
                orderItem.getLastReview(),
                orderItem.getAnalyzedAt(),
                orderItem.getCompletedAt(),
                orderItem.getPayedAt()
        );
    }
}