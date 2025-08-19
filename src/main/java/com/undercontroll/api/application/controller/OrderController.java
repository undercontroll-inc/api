package com.undercontroll.api.application.controller;

import com.undercontroll.api.application.port.OrderPort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/orders")
public class OrderController {

    private final OrderPort orderPort;

    public OrderController(OrderPort orderPort) {
        this.orderPort = orderPort;
    }

}
