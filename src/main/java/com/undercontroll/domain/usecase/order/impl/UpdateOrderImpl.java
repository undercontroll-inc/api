package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.application.dto.demand.CreateDemandRequest;
import com.undercontroll.application.dto.order.PartDto;
import com.undercontroll.application.dto.order.UpdateOrderRequest;
import com.undercontroll.application.dto.orderitem.UpdateOrderItemDto;
import com.undercontroll.application.dto.orderitem.UpdateOrderItemRequest;
import com.undercontroll.domain.model.Demand;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.domain.exception.OrderNotFoundException;
import com.undercontroll.domain.exception.InvalidUpdateOrderException;
import com.undercontroll.domain.gateway.DemandGateway;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.usecase.demand.CreateDemandPort;
import com.undercontroll.domain.usecase.order.UpdateOrderPort;
import com.undercontroll.domain.usecase.order_item.UpdateOrderItemPort;
import com.undercontroll.infrastructure.service.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrderImpl implements UpdateOrderPort {

    private final OrderGateway orderGateway;
    private final MetricsService metricsService;
    private final UpdateOrderItemPort updateOrderItemPort;
    private final DemandGateway demandGateway;
    private final CreateDemandPort createDemandPort;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"orders", "ordersByUser", "order", "orderParts", "dashboardMetrics"}, allEntries = true)
    public void execute(Integer orderId, UpdateOrderRequest request) {
        try {
            log.info("Updating order {}", orderId);

            validateUpdateOrder(orderId);

            Order order = orderGateway.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException("Could not found the order while updating."));

            if (request.status() != null) {
                order.setStatus(request.status());
                log.info("Order {} status updated to {}", orderId, request.status());
                if (request.status() == OrderStatus.COMPLETED) {
                    metricsService.incrementOrderCompleted();
                }
            }

            if (request.serviceDescription() != null) {
                order.setDescription(request.serviceDescription());
            }

            if (request.appliances() != null) {
                for (UpdateOrderItemDto dto : request.appliances()) {
                    if (dto.id() != null) {
                        updateOrderItemPort.execute(orderId, dto.id(), new UpdateOrderItemRequest(
                                null,
                                dto.laborValue(),
                                dto.customerNote(),
                                dto.volt(),
                                dto.series(),
                                dto.type(),
                                dto.brand(),
                                dto.model(),
                                null
                        ));
                    } else {
                        OrderItem novo = OrderItem.builder()
                                .type(dto.type())
                                .brand(dto.brand())
                                .model(dto.model())
                                .volt(dto.volt())
                                .series(dto.series())
                                .observation(dto.customerNote())
                                .laborValue(dto.laborValue() != null ? dto.laborValue() : 0.0)
                                .build();
                        order.addOrderItem(novo);
                    }
                }
            }

            if (request.parts() != null) {
                for (PartDto p : request.parts()) {
                    if (p.componentId() == null) continue;
                    Optional<Demand> existing = demandGateway.findByOrderAndComponentId(order, p.componentId());
                    boolean keep = p.quantity() != null && p.quantity() > 0;
                    if (existing.isPresent()) {
                        if (keep) {
                            Demand d = existing.get();
                            d.setQuantity(p.quantity().longValue());
                            demandGateway.save(d);
                        } else {
                            demandGateway.deleteById(existing.get().getId());
                        }
                    } else if (keep) {
                        createDemandPort.execute(order.getId(), new CreateDemandRequest(
                                p.componentId(),
                                p.quantity().longValue()
                        ));
                    }
                }
            }

            orderGateway.save(order);
            log.info("Order {} updated successfully", orderId);
        } catch (Exception e) {
            metricsService.incrementOrderUpdateFailed();
            throw e;
        }
    }

    private void validateUpdateOrder(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidUpdateOrderException("Order id cannot be null for the update");
        }
    }
}
