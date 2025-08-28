package com.undercontroll.api.application.port;

import com.undercontroll.api.application.dto.OrderDto;
import com.undercontroll.api.application.dto.UpdateOrderRequest;
import com.undercontroll.api.domain.model.Order;

import java.util.List;

public interface OrderPort {

    Order createOrder();
    void updateOrder(UpdateOrderRequest request);
    List<OrderDto> getOrders();
    void deleteOrder(Integer orderId);

}
