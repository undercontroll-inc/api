package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.dto.CreateServiceOrderRequest;
import com.undercontroll.api.application.dto.CreateServiceOrderResponse;
import com.undercontroll.api.application.dto.ServiceOrderDto;
import com.undercontroll.api.application.dto.UpdateServiceOrderRequest;
import com.undercontroll.api.application.port.ServiceOrderPort;

import java.util.List;

public class ServiceOrderService implements ServiceOrderPort {

    @Override
    public CreateServiceOrderResponse createServiceOrder(CreateServiceOrderRequest createServiceOrderRequest) {
        return null;
    }

    @Override
    public void updateServiceOrder(UpdateServiceOrderRequest data) {

    }

    @Override
    public List<ServiceOrderDto> getServiceOrders() {
        return List.of();
    }

    @Override
    public ServiceOrderDto getServiceOrderById(Integer serviceOrderId) {
        return null;
    }

    @Override
    public void deleteServiceOrder(Integer serviceOrderId) {

    }

    @Override
    public List<ServiceOrderDto> getServiceOrdersByOrderId(Integer orderId) {
        return List.of();
    }
}
