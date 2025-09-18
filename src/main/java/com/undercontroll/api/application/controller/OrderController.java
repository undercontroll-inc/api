package com.undercontroll.api.application.controller;

import com.undercontroll.api.application.dto.OrderDto;
import com.undercontroll.api.application.dto.UpdateOrderRequest;
import com.undercontroll.api.application.port.OrderPort;
import com.undercontroll.api.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderPort orderPort;

    @PostMapping
    public ResponseEntity<Order> createOrder(
    ) {
        Order order = orderPort.createOrder();

        return ResponseEntity.status(201).body(order);
    }

    @PutMapping
    public ResponseEntity<Void> updateOrder(
            @RequestBody UpdateOrderRequest request
    ) {
        orderPort.updateOrder(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders() {
        List<OrderDto> orders = orderPort.getOrders();

        return orders.isEmpty() ?  ResponseEntity.noContent().build() : ResponseEntity.ok(orders);
    }

    @DeleteMapping("{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Integer orderId
    ) {
        orderPort.deleteOrder(orderId);

        return ResponseEntity.noContent().build();
    }


}
