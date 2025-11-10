package com.undercontroll.api.model;

import com.undercontroll.api.dto.CreateOrderRequest;
import com.undercontroll.api.dto.OrderDto;
import com.undercontroll.api.dto.UpdateOrderRequest;
import com.undercontroll.api.model.Order;

import java.util.List;

public interface OrderPort {

    Order createOrder(CreateOrderRequest request);
    void updateOrder(UpdateOrderRequest request);
    List<OrderDto> getOrders();
    void deleteOrder(Integer orderId);

}
