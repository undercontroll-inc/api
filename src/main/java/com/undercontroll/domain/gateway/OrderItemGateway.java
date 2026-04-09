package com.undercontroll.domain.gateway;

import com.undercontroll.domain.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemGateway {

    OrderItem save(OrderItem orderItem);

    void deleteById(Integer id);

    Optional<OrderItem> findById(Integer id);

    List<OrderItem> findAll();

}
