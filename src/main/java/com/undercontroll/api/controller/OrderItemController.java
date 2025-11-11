package com.undercontroll.api.controller;

import com.undercontroll.api.dto.CreateOrderItemRequest;
import com.undercontroll.api.dto.OrderItemDto;
import com.undercontroll.api.dto.UpdateOrderItemRequest;
import com.undercontroll.api.service.OrderItemService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService service;

    @PostMapping
    public ResponseEntity<OrderItemDto> createOrderItem(
            @RequestBody CreateOrderItemRequest request
    ) {
        OrderItemDto orderItem = service.createOrderItem(request);

        log.info("Order item:  {}", orderItem);

        return ResponseEntity.status(201).body(orderItem);
    }

    @PutMapping
    public ResponseEntity<Void> updateOrderItem(
            @RequestBody UpdateOrderItemRequest request
    ) {
        service.updateOrderItem(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDto>> getOrderItems() {
        List<OrderItemDto> orderItems = service.getOrderItems();
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<OrderItemDto> getOrderItemById(
            @PathVariable Integer orderItemId
    ) {
        OrderItemDto orderItem = service.getOrderItemById(orderItemId);
        return ResponseEntity.ok(orderItem);
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable Integer orderItemId
    ) {
        service.deleteOrderItem(orderItemId);
        return ResponseEntity.ok().build();
    }
}