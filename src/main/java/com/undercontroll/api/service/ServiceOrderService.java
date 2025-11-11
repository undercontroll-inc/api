package com.undercontroll.api.service;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.dto.ServiceOrderDto;
import com.undercontroll.api.exception.InvalidServiceOrderException;
import com.undercontroll.api.exception.ServiceOrderNotFoundException;
import com.undercontroll.api.model.Order;
import com.undercontroll.api.model.ServiceOrder;
import com.undercontroll.api.repository.OrderJpaRepository;
import com.undercontroll.api.repository.ServiceOrderJpaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderJpaRepository repository;
    private final PdfExportService pdfExportService;
    private final OrderJpaRepository orderRepository;

    public CreateServiceOrderResponse createServiceOrder(
            @Valid CreateServiceOrderRequest request
    ) {

        Optional<Order> order = orderRepository.findById(request.orderId());

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

        ServiceOrder serviceOrderSaved = repository.save(serviceOrder);

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

    public void updateServiceOrder(
            @Valid UpdateServiceOrderRequest request
    ) {
        ServiceOrder serviceOrder = repository.findById(request.serviceOrderId())
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

        repository.save(serviceOrder);

    }

    public List<ServiceOrderDto> getServiceOrders(
    ) {
        return repository
                .findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public ServiceOrderDto getServiceOrderById(
            @NotNull @Positive Integer serviceOrderId
    ) {
        ServiceOrder serviceOrder = repository.findById(serviceOrderId)
                .orElseThrow(() -> new ServiceOrderNotFoundException("Service order not found for ID: " + serviceOrderId));
        return mapToDto(serviceOrder);
    }

    public void deleteServiceOrder(
            @NotNull @Positive Integer serviceOrderId
    ) {
        validateDeleteOrder(serviceOrderId);

        Optional<ServiceOrder> orderFound = repository.findById(serviceOrderId);

        if(orderFound.isEmpty()) {
            throw new ServiceOrderNotFoundException("Could not found the Service order");
        }

        repository.delete(orderFound.get());
    }

    public List<ServiceOrderDto> getServiceOrdersByOrderId(
            @NotNull @Positive Integer orderId
    ) {
        List<ServiceOrder> serviceOrders = repository.findByOrder_Id(orderId);
        if(serviceOrders.isEmpty()) {
            throw new ServiceOrderNotFoundException("No service orders found for the given order ID: " + orderId);
        }
        return serviceOrders.stream()
                .map(this::mapToDto)
                .toList();
    }

    public void exportServiceOrder(Integer id) {
        Optional<ServiceOrder> orderFound = repository.findById(id);

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