package com.undercontroll.api.domain.service;

import com.undercontroll.api.application.dto.*;
import com.undercontroll.api.application.port.ServiceOrderPort;
import com.undercontroll.api.domain.exceptions.InvalidServiceOrderException;
import com.undercontroll.api.domain.exceptions.ServiceOrderNotFoundException;
import com.undercontroll.api.domain.model.Order;
import com.undercontroll.api.domain.model.ServiceOrder;
import com.undercontroll.api.infrastructure.pdf.PdfExportService;
import com.undercontroll.api.infrastructure.persistence.adapter.OrderPersistenceAdapter;
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
    private final PdfExportService pdfExportService;
    private final OrderPersistenceAdapter orderAdapter;

    @Override
    public CreateServiceOrderResponse createServiceOrder(
            @Valid CreateServiceOrderRequest request
    ) {

        Optional<Order> order = orderAdapter.findOrderById(request.orderId());

        if(order.isEmpty()) {
            throw new InvalidServiceOrderException("Could not found the order associated with id: %d while creating the service order".formatted(request.orderId()));
        }

        ServiceOrder serviceOrder = ServiceOrder.builder()
                .order(order.get())
                .nf(request.nf())
                .date(request.date())
                .issue(request.issue())
                .store(request.store())
                .description(request.description())
                .fabricGuarantee(request.fabricGuarantee())
                .returnGuarantee(request.returnGuarantee())
                .received_at(request.received_at())
                .withdraw_at(request.withdrawalAt())
                .build();

        ServiceOrder serviceOrderSaved = adapter.saveServiceOrder(serviceOrder);

        return CreateServiceOrderResponse.builder()
                .order(serviceOrderSaved.getOrder())
                .nf(serviceOrderSaved.getNf())
                .date(serviceOrderSaved.getDate())
                .issue(serviceOrderSaved.getIssue())
                .store(serviceOrderSaved.getStore())
                .description(serviceOrderSaved.getDescription())
                .fabricGuarantee(serviceOrderSaved.getFabricGuarantee())
                .returnGuarantee(serviceOrderSaved.getReturnGuarantee())
                .receivedAt(serviceOrderSaved.getReceived_at())
                .withdrawAt(serviceOrderSaved.getWithdraw_at())
                .build();


    }

    @Override
    public void updateServiceOrder(
            @Valid UpdateServiceOrderRequest request
    ) {
        ServiceOrder serviceOrder = adapter.findServiceOrderById(request.serviceOrderId())
                .orElseThrow(() -> new ServiceOrderNotFoundException("Service order not found for ID: " + request.serviceOrderId()));


        // Validar isso
//        Optional.ofNullable(request.user()).ifPresent(serviceOrder::setUser);
//        Optional.ofNullable(request.componentPartList()).ifPresent(serviceOrder::setComponentPartList);
        Optional.ofNullable(request.order()).ifPresent(serviceOrder::setOrder);
        Optional.ofNullable(request.fabricGuarantee()).ifPresent(serviceOrder::setFabricGuarantee);
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

    @Override
    public void exportServiceOrder(Integer id) {
        Optional<ServiceOrder> orderFound = adapter.findServiceOrderById(id);

        if(orderFound.isEmpty()) {
            throw new ServiceOrderNotFoundException("Could not found the Service order");
        }

        ServiceOrderDto dto = mapToDto(orderFound.get());

        pdfExportService.exportOS(dto);

    }

    private void validateDeleteOrder(Integer serviceOrderId) {
        if(serviceOrderId == null || serviceOrderId <= 0) {
            throw new InvalidServiceOrderException("Service order ID must be a positive integer");
        }
    }

    private ServiceOrderDto mapToDto(ServiceOrder serviceOrder) {
        return ServiceOrderDto.builder()
                .nf(serviceOrder.getNf())
                .date(serviceOrder.getDate())
                .issue(serviceOrder.getIssue())
                .store(serviceOrder.getStore())
                .description(serviceOrder.getDescription())
                .fabricGuarantee(serviceOrder.getFabricGuarantee())
                .returnGuarantee(serviceOrder.getReturnGuarantee())
                .receivedAt(serviceOrder.getReceived_at())
                .withDrawAt(serviceOrder.getWithdraw_at())
                .build();
    }
}