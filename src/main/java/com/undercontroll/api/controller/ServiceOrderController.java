package com.undercontroll.api.controller;

import com.undercontroll.api.dto.*;
import com.undercontroll.api.service.ServiceOrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/service-orders")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderPort serviceOrderPort;

    @PostMapping
    public ResponseEntity<CreateServiceOrderResponse> createServiceOrder(
            @RequestBody CreateServiceOrderRequest request
    ) {
        CreateServiceOrderResponse serviceOrder = serviceOrderPort.createServiceOrder(request);

        return ResponseEntity.status(201).body(serviceOrder);
    }

    @PutMapping
    public ResponseEntity<Void> updateServiceOrder(
            @RequestBody UpdateServiceOrderRequest request
    ) {
        serviceOrderPort.updateServiceOrder(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ServiceOrderDto>> getServiceOrders() {
        List<ServiceOrderDto> serviceOrders = serviceOrderPort.getServiceOrders();

        return ResponseEntity.ok(serviceOrders);
    }

    @GetMapping("/{serviceOrderId}")
    public ResponseEntity<ServiceOrderDto> getServiceOrdersById(
            @PathVariable Integer serviceOrderId
    ) {
        ServiceOrderDto serviceOrder = serviceOrderPort.getServiceOrderById(serviceOrderId);
        return ResponseEntity.ok(serviceOrder);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ServiceOrderDto>> getServiceOrdersByOrderId(
            @PathVariable Integer orderId
    ) {
        List<ServiceOrderDto> serviceOrders = serviceOrderPort.getServiceOrdersByOrderId(orderId);
        return ResponseEntity.ok(serviceOrders);
    }

    @DeleteMapping("/{serviceOrderId}")
    public ResponseEntity<Void> deleteServiceOrder(
            @PathVariable Integer serviceOrderId
    ) {
        serviceOrderPort.deleteServiceOrder(serviceOrderId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<Void> exportServiceOrder(
            @PathVariable Integer id
    ) {
        serviceOrderPort.exportServiceOrder(id);

        return ResponseEntity.ok().build();
    }

}
