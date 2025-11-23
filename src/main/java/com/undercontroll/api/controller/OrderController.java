package com.undercontroll.api.controller;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.model.Order;
import com.undercontroll.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
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

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateOrder(
            @PathVariable Integer id,
            @RequestBody UpdateOrderRequest request
    ) {
        orderService.updateOrder(request, id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<GetAllOrdersResponse> getOrders() {
        GetAllOrdersResponse orders = orderService.getOrders();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<GetOrderByIdResponse> getOrderById(
            @PathVariable Integer orderId
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = null;
        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof Jwt jwt) {
                email = jwt.getClaimAsString("sub");
            }
        }
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        GetOrderByIdResponse response = orderService.getOrderById(orderId, email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Integer orderId
    ) {
        orderService.deleteOrder(orderId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<GetOrdersByUserIdResponse> getOrdersByUserId(
            @RequestParam(value = "userId") Integer userId
    ) {

        log.info("Oie");
        GetOrdersByUserIdResponse response = orderService.getOrdersByUserId(userId);

        return ResponseEntity.ok(response);
    }

}
