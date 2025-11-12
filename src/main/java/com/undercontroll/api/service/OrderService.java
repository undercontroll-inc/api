package com.undercontroll.api.service;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.exception.InvalidDeleteOrderException;
import com.undercontroll.api.exception.InvalidUpdateOrderException;
import com.undercontroll.api.exception.OrderNotFoundException;
import com.undercontroll.api.model.Order;
import com.undercontroll.api.model.OrderItem;
import com.undercontroll.api.model.OrderStatus;
import com.undercontroll.api.repository.DemandRepository;
import com.undercontroll.api.repository.OrderItemJpaRepository;
import com.undercontroll.api.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderJpaRepository repository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final DemandRepository demandRepository;

    public Order createOrder(
            CreateOrderRequest request
    ) {
        List<OrderItem> orderItems = new ArrayList<>();

        if(request.orderItemIds() != null && !request.orderItemIds().isEmpty()) {
            orderItems = orderItemJpaRepository.findAllById(request.orderItemIds());
        }

        Order order = Order.builder()
                .total(0.0)
                .discount(0.0)
                .orderItems(orderItems)
                .status(OrderStatus.PENDING)
                .build();

        return repository.save(order);
    }

    public void updateOrder(UpdateOrderRequest request) {
        validateUpdateOrder(request);

        Optional<Order> orderFound = repository.findById(request.id());

        if(orderFound.isEmpty()) {
            throw new OrderNotFoundException("Could not found the order");
        }

        if(request.completedTime() != null) {
            orderFound.get().setCompletedTime(request.completedTime());
        }

        if(request.startedAt() != null) {
            orderFound.get().setStartedAt(request.startedAt());
        }

        repository.save(orderFound.get());
    }

    public List<OrderDto> getOrders() {
        return repository
                .findAll()
                .stream()
                .map(o -> new OrderDto(
                        o.getOrderItems()
                                .stream()
                                .map(i -> new OrderItemDto(
                                        i.getName(),
                                        i.getImageUrl(),
                                        i.getObservation(),
                                        i.getVolt(),
                                        i.getSeries(),
                                        i.getLastReview(),
                                        i.getAnalyzedAt(),
                                        i.getCompletedAt()
                                ))
                                .toList(),
                        o.getCreatedAt(),
                        o.getStartedAt(),
                        o.getCompletedTime(),
                        o.getStatus(),
                        o.getLaborValue()
                ))
                .toList();
    }

    public void deleteOrder(Integer orderId) {
        validateDeleteOrder(orderId);

        Optional<Order> orderFound = repository.findById(orderId);

        if(orderFound.isEmpty()) {
            throw new OrderNotFoundException("Could not found the order");
        }

        repository.delete(orderFound.get());
    }

    public GetOrdersByUserIdResponse getOrdersByUserId(Integer userId) {
        List<Order> orders = repository.findByUser_id(userId);

        return new GetOrdersByUserIdResponse(
                orders.stream().map(o -> {
                    Double partsTotal = repository.calculatePartsTotalByUserId(userId);

                    Double total = partsTotal + o.getLaborValue() - o.getDiscount();

                    return new OrderEnrichedDto(
                            o.getId(),
                            userId,
                            o.getOrderItems()
                            .stream()
                            .map(
                                    i -> new OrderItemDto(
                                    i.getName(),
                                    i.getImageUrl(),
                                    i.getObservation(),
                                    i.getVolt(),
                                    i.getSeries(),
                                    i.getLastReview(),
                                    i.getAnalyzedAt(),
                                    i.getCompletedAt()
                                    ))
                            .toList(),
                            partsTotal,
                            o.getLaborValue(),
                            o.getDiscount(),
                            total,
                            o.getCreatedAt(),
                            o.getCompletedTime(),
                            o.get
                    )
                })
        )
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
