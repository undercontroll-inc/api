package com.undercontroll.api.controller;

import com.undercontroll.api.dto.CreateOrderItemRequest;
import com.undercontroll.api.dto.OrderItemDto;
import com.undercontroll.api.dto.UpdateOrderItemRequest;
import com.undercontroll.api.model.OrderItemPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private static final Logger log = LoggerFactory.getLogger(OrderItemController.class);
    private final OrderItemPort orderItemPort;

    @PostMapping
    public ResponseEntity<OrderItemDto> createOrderItem(
            @RequestBody CreateOrderItemRequest request
    ) {
        OrderItemDto orderItem = orderItemPort.createOrderItem(request);

        log.info("Order item:  {}", orderItem);

        return ResponseEntity.status(201).body(orderItem);
    }

    @PutMapping
    public ResponseEntity<Void> updateOrderItem(
            @RequestBody UpdateOrderItemRequest request
    ) {
        orderItemPort.updateOrderItem(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDto>> getOrderItems() {
        List<OrderItemDto> orderItems = orderItemPort.getOrderItems();
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<OrderItemDto> getOrderItemById(
            @PathVariable Integer orderItemId
    ) {
        OrderItemDto orderItem = orderItemPort.getOrderItemById(orderItemId);
        return ResponseEntity.ok(orderItem);
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable Integer orderItemId
    ) {
        orderItemPort.deleteOrderItem(orderItemId);
        return ResponseEntity.ok().build();
    }
}