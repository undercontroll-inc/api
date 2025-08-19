package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.port.OrderPort;
import com.undercontroll.api.infrastructure.persistence.adapter.OrderPersistenceAdapter;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements OrderPort {

    private final OrderPersistenceAdapter adapter;

    public OrderService(OrderPersistenceAdapter adapter) {
        this.adapter = adapter;
    }


}
