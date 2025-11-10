package com.undercontroll.api.service;

import com.undercontroll.api.dto.CreateOrderItemRequest;
import com.undercontroll.api.dto.OrderItemDto;
import com.undercontroll.api.dto.UpdateOrderItemRequest;
import com.undercontroll.api.model.OrderItemPort;
import com.undercontroll.api.exception.InvalidOrderItemException;
import com.undercontroll.api.model.OrderItem;
import com.undercontroll.api.model.OrderItemPersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService implements OrderItemPort {

    private final OrderItemPersistenceAdapter orderItemAdapter;

    @Override
    public OrderItemDto createOrderItem(CreateOrderItemRequest request) {
        validateCreateOrderItemRequest(request);

        OrderItem orderItem = OrderItem.builder()
                .name(request.name())
                .imageUrl(request.imageUrl())
                .status(request.status())
                .observation(request.observation())
                .labor(request.labor())
                .volt(request.volt())
                .series(request.series())
                .demands(new ArrayList<>())
                .build();

        OrderItem orderItemSaved = orderItemAdapter.saveOrderItem(orderItem);

        return mapToDto(orderItemSaved);
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
        if (data.labor() != null) {
            orderItem.setLabor(data.labor());
        }
        if (data.observation() != null) {
            orderItem.setObservation(data.observation());
        }
        if (data.volt() != null) {
            orderItem.setVolt(data.volt());
        }
        if (data.series() != null) {
            orderItem.setSeries(data.series());
        }
        if (data.status() != null) {
            orderItem.setStatus(data.status());
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
//
//    @Override
//    public List<OrderItemDto> getOrderItemsByOrderId(Integer orderId) {
//        if (orderId == null) {
//            throw new InvalidOrderItemException("Order ID cannot be null");
//        }
//
//        return orderItemAdapter
//                .getOrderItemsByOrderId(orderId)
//                .stream()
//                .map(this::mapToDto)
//                .toList();
//    }

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

        if (request.labor() != null && request.labor() < 0) {
            throw new InvalidOrderItemException("Order item labor cannot be negative");
        }
    }

    private void validateUpdateOrderItem(UpdateOrderItemRequest orderItem) {
        if (orderItem.id() == null) {
            throw new InvalidOrderItemException("Order item ID cannot be null for update");
        }

        if (orderItem.name() != null && orderItem.name().trim().isEmpty()) {
            throw new InvalidOrderItemException("Order item name cannot be empty");
        }

        if (orderItem.labor() != null && orderItem.labor() < 0) {
            throw new InvalidOrderItemException("Order item labor cannot be negative");
        }
    }

    private OrderItemDto mapToDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getName(),
                orderItem.getImageUrl(),
                orderItem.getLabor(),
                orderItem.getObservation(),
                orderItem.getVolt(),
                orderItem.getSeries(),
                orderItem.getStatus(),
                orderItem.getLastReview(),
                orderItem.getAnalyzedAt(),
                orderItem.getCompletedAt()
        );
    }
}