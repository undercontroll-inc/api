package com.undercontroll.api.service;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.dto.ServiceOrderDto;
import com.undercontroll.api.dto.UpdateServiceOrderRequest;

import java.util.List;

public interface ServiceOrderPort {

    CreateServiceOrderResponse createServiceOrder(CreateServiceOrderRequest createServiceOrderRequest);
    void updateServiceOrder(UpdateServiceOrderRequest data);
    List<ServiceOrderDto> getServiceOrders();
    ServiceOrderDto getServiceOrderById(Integer serviceOrderId);
    void deleteServiceOrder(Integer serviceOrderId);
    List<ServiceOrderDto> getServiceOrdersByOrderId(Integer orderId);
    void exportServiceOrder(Integer id);
}
