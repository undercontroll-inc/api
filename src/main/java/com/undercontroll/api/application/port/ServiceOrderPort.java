package com.undercontroll.api.application.port;

import com.undercontroll.api.application.dto.*;

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
