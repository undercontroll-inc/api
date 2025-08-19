package com.undercontroll.api.infrastructure.persistence.adapter;

import com.undercontroll.api.infrastructure.persistence.mapper.OrderMapper;
import com.undercontroll.api.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderPersistenceAdapter {

    private final OrderJpaRepository repository;
    private final OrderMapper mapper;

    public OrderPersistenceAdapter(OrderJpaRepository repository, OrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }



}
