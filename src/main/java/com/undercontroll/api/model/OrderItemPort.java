package com.undercontroll.api.model;

import com.undercontroll.api.dto.CreateOrderItemRequest;
import com.undercontroll.api.dto.OrderItemDto;
import com.undercontroll.api.dto.UpdateOrderItemRequest;
import java.util.List;

public interface OrderItemPort {

    OrderItemDto createOrderItem(CreateOrderItemRequest createOrderItemRequest);
    void updateOrderItem(UpdateOrderItemRequest data);
    List<OrderItemDto> getOrderItems();
//    List<OrderItemDto> getOrderItemsByOrderId(Integer orderId);
    void deleteOrderItem(Integer orderItemId);
    OrderItemDto getOrderItemById(Integer orderItemId);

}