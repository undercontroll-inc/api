package com.undercontroll.api.application.port;

import com.undercontroll.api.application.dto.CreateOrderItemRequest;
import com.undercontroll.api.application.dto.OrderItemDto;
import com.undercontroll.api.application.dto.UpdateOrderItemRequest;
import java.util.List;

public interface OrderItemPort {

    OrderItemDto createOrderItem(CreateOrderItemRequest createOrderItemRequest);
    void updateOrderItem(UpdateOrderItemRequest data);
    List<OrderItemDto> getOrderItems();
    List<OrderItemDto> getOrderItemsByOrderId(Integer orderId);
    void deleteOrderItem(Integer orderItemId);
    OrderItemDto getOrderItemById(Integer orderItemId);

}