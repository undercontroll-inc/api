package com.undercontroll.api.application.port;

import com.undercontroll.api.application.dto.CreateOrderRequest;
import com.undercontroll.api.application.dto.OrderDto;
import com.undercontroll.api.domain.model.Order;

import java.util.List;

public interface OrderPort {

    Order createOrder(CreateOrderRequest createOrderRequest);
    void updateOrder(Order order);
    List<OrderDto> getOrders();
    void deleteOrder(Integer orderId);

}
