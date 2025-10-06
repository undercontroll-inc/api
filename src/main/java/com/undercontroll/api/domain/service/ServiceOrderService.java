package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.dto.*;
import com.undercontroll.api.application.port.ServiceOrderPort;
import com.undercontroll.api.domain.exceptions.InvalidServiceOrderException;
import com.undercontroll.api.domain.exceptions.ServiceOrderNotFoundException;
import com.undercontroll.api.domain.model.ServiceOrder;
import com.undercontroll.api.infrastructure.persistence.adapter.ServiceOrderPersistenceAdapter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceOrderService implements ServiceOrderPort {

    private final ServiceOrderPersistenceAdapter adapter;

    @Override
    public CreateServiceOrderResponse createServiceOrder(
            @Valid CreateServiceOrderRequest request
    ) {

        ServiceOrder serviceOrder = ServiceOrder.builder()
                .order(request.order())
                .nf(request.nf())
                .date(request.date())
                .issue(request.issue())
                .store(request.store())
                .budget(request.budget())
                .user(request.user())
                .componentPartList(request.componentPartList())
                .description(request.description())
                .fabricGuarantee(request.fabricGuarantee())
                .returnGuarantee(request.returnGuarantee())
                .build();

        ServiceOrder serviceOrderSaved = adapter.saveServiceOrder(serviceOrder);

        return CreateServiceOrderResponse.builder()
                .order(serviceOrderSaved.getOrder())
                .nf(serviceOrderSaved.getNf())
                .date(serviceOrderSaved.getDate())
                .issue(serviceOrderSaved.getIssue())
                .store(serviceOrderSaved.getStore())
                .budget(serviceOrderSaved.getBudget())
                .user(serviceOrderSaved.getUser())
                .componentPartList(serviceOrderSaved.getComponentPartList())
                .description(serviceOrderSaved.getDescription())
                .fabricGuarantee(serviceOrderSaved.getFabricGuarantee())
                .returnGuarantee(serviceOrderSaved.getReturnGuarantee())
                .build();


    }

    @Override
    public void updateServiceOrder(
            @Valid UpdateServiceOrderRequest request
    ) {
        ServiceOrder serviceOrder = adapter.findServiceOrderById(request.serviceOrderId())
                .orElseThrow(() -> new ServiceOrderNotFoundException("Service order not found for ID: " + request.serviceOrderId()));

        Optional.ofNullable(request.user()).ifPresent(serviceOrder::setUser);
        Optional.ofNullable(request.componentPartList()).ifPresent(serviceOrder::setComponentPartList);
        Optional.ofNullable(request.order()).ifPresent(serviceOrder::setOrder);
        Optional.ofNullable(request.fabricGuarantee()).ifPresent(serviceOrder::setFabricGuarantee);
        Optional.ofNullable(request.budget()).ifPresent(serviceOrder::setBudget);
        Optional.ofNullable(request.returnGuarantee()).ifPresent(serviceOrder::setReturnGuarantee);
        Optional.ofNullable(request.description()).ifPresent(serviceOrder::setDescription);
        Optional.ofNullable(request.nf()).ifPresent(serviceOrder::setNf);
        Optional.ofNullable(request.date()).ifPresent(serviceOrder::setDate);
        Optional.ofNullable(request.store()).ifPresent(serviceOrder::setStore);
        Optional.ofNullable(request.issue()).ifPresent(serviceOrder::setIssue);

        adapter.updateServiceOrder(serviceOrder);

    }

    @Override
    public List<ServiceOrderDto> getServiceOrders(
    ) {
        return adapter
                .getServiceOrders()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ServiceOrderDto getServiceOrderById(
            @NotNull @Positive Integer serviceOrderId
    ) {
        ServiceOrder serviceOrder = adapter.findServiceOrderById(serviceOrderId)
                .orElseThrow(() -> new ServiceOrderNotFoundException("Service order not found for ID: " + serviceOrderId));
        return mapToDto(serviceOrder);
    }

    @Override
    public void deleteServiceOrder(
            @NotNull @Positive Integer serviceOrderId
    ) {
        validateDeleteOrder(serviceOrderId);

        Optional<ServiceOrder> orderFound = adapter.findServiceOrderById(serviceOrderId);

        if(orderFound.isEmpty()) {
            throw new ServiceOrderNotFoundException("Could not found the Service order");
        }

        adapter.deleteServiceOrder(orderFound.get());
    }

    @Override
    public List<ServiceOrderDto> getServiceOrdersByOrderId(
            @NotNull @Positive Integer orderId
    ) {
        List<ServiceOrder> serviceOrders = adapter.findServiceOrdersByOrderId(orderId);
        if(serviceOrders.isEmpty()) {
            throw new ServiceOrderNotFoundException("No service orders found for the given order ID: " + orderId);
        }
        return serviceOrders.stream()
                .map(this::mapToDto)
                .toList();
    }

    private void validateDeleteOrder(Integer serviceOrderId) {
        if(serviceOrderId == null || serviceOrderId <= 0) {
            throw new InvalidServiceOrderException("Service order ID must be a positive integer");
        }
    }

    private ServiceOrderDto mapToDto(ServiceOrder serviceOrder) {
        return ServiceOrderDto.builder()
                .order(serviceOrder.getOrder())
                .nf(serviceOrder.getNf())
                .date(serviceOrder.getDate())
                .issue(serviceOrder.getIssue())
                .store(serviceOrder.getStore())
                .budget(serviceOrder.getBudget())
                .user(serviceOrder.getUser())
                .componentPartList(serviceOrder.getComponentPartList())
                .description(serviceOrder.getDescription())
                .fabricGuarantee(serviceOrder.getFabricGuarantee())
                .returnGuarantee(serviceOrder.getReturnGuarantee())
                .build();
    }
}