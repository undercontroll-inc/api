package com.undercontroll.api.service;

import com.undercontroll.api.dto.CreateOrderItemRequest;
import com.undercontroll.api.dto.OrderItemDto;
import com.undercontroll.api.dto.UpdateOrderItemRequest;
import com.undercontroll.api.exception.InvalidOrderItemException;
import com.undercontroll.api.exception.OrderItemNotFoundException;
import com.undercontroll.api.model.OrderItem;
import com.undercontroll.api.repository.OrderItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemJpaRepository repository;

    public OrderItem createOrderItem(CreateOrderItemRequest request) {
        validateCreateOrderItemRequest(request);

        OrderItem orderItem = OrderItem.builder()
                .brand(request.brand())
                .model(request.model())
                .type(request.type())
                .imageUrl(request.imageUrl())
                .observation(request.observation())
                .volt(request.volt())
                .series(request.series())
                .laborValue(request.laborValue())
                .build();

        return repository.save(orderItem);
    }

    public void updateOrderItem(UpdateOrderItemRequest data) {
        validateUpdateOrderItem(data);

        Optional<OrderItem> orderItem = repository.findById(data.id());

        if(orderItem.isEmpty()) {
            throw new OrderItemNotFoundException("Could not found the order for update with id: %s".formatted(data.id()));
        }

        OrderItem orderFound = orderItem.get();

        // Faltando aqui validar os campos model, brand e type
        if (data.imageUrl() != null) {
            orderFound.setImageUrl(data.imageUrl());
        }
        if (data.observation() != null) {
            orderFound.setObservation(data.observation());
        }
        if (data.volt() != null) {
            orderFound.setVolt(data.volt());
        }
        if (data.series() != null) {
            orderFound.setSeries(data.series());
        }
        if (data.lastReview() != null) {
            orderFound.setLastReview(data.lastReview());
        }
        if (data.analyzedAt() != null) {
            orderFound.setAnalyzedAt(data.analyzedAt());
        }
        if (data.completedAt() != null) {
            orderFound.setCompletedAt(data.completedAt());
        }

        repository.save(orderFound);
    }
//
//
//    public List<OrderItemDto> getOrderItemsByOrderId(Integer orderId) {
//        if (orderId == null) {
//            throw new InvalidOrderItemException("Order ID cannot be null");
//        }
//
//        return repository
//                .getOrderItemsByOrderId(orderId)
//                .stream()
//                .map(this::mapToDto)
//                .toList();
//    }

    public void deleteOrderItem(Integer orderItemId) {
        if (orderItemId == null) {
            throw new InvalidOrderItemException("Order item ID cannot be null");
        }

        repository.findById(orderItemId).ifPresent(repository::delete);
    }

    public OrderItemDto getOrderItemById(Integer orderItemId) {
        if (orderItemId == null) {
            throw new InvalidOrderItemException("Order item ID cannot be null");
        }

        OrderItem orderItem = repository.findById(orderItemId).orElseThrow(
                () -> new OrderItemNotFoundException("Could not found the order item with id: %s".formatted(orderItemId))
        );
        return mapToDto(orderItem);
    }

    public List<OrderItemDto> getOrderItems() {
        return repository
                .findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private void validateCreateOrderItemRequest(CreateOrderItemRequest request) {
        if (request.laborValue() != null && request.laborValue() < 0) {
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

    public OrderItemDto mapToDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getImageUrl(),
                orderItem.getModel(),
                orderItem.getType(),
                orderItem.getBrand(),
                orderItem.getObservation(),
                orderItem.getVolt(),
                orderItem.getSeries(),
                orderItem.getLastReview(),
                orderItem.getAnalyzedAt(),
                orderItem.getCompletedAt()
        );
    }
}