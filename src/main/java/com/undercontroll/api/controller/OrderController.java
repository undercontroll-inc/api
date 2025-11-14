package com.undercontroll.api.controller;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.model.Order;
import com.undercontroll.api.service.OrderService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody CreateOrderRequest createOrderRequest
    ) {
        Order order = orderService.createOrder(createOrderRequest);

        return ResponseEntity.status(201).body(order);
    }

    @PutMapping
    public ResponseEntity<Void> updateOrder(
            @RequestBody UpdateOrderRequest request
    ) {
        orderService.updateOrder(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<GetAllOrdersResponse> getOrders() {
        GetAllOrdersResponse orders = orderService.getOrders();

        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Integer orderId
    ) {
        orderService.deleteOrder(orderId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("{userId}")
    public ResponseEntity<GetOrdersByUserIdResponse> getOrdersByUserId(
            @RequestParam(value = "userId", required = false) Integer userId
    ) {
        GetOrdersByUserIdResponse response = orderService.getOrdersByUserId(userId);

        return ResponseEntity.ok(response);
    }




}
