package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.application.dto.PartDto;
import com.undercontroll.application.dto.UpdateOrderItemDto;
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
    public Output execute(Input input) {
        try {
            log.info("Updating order {}", input.orderId());

            validateUpdateOrder(input.orderId());

            Order order = orderGateway.findById(input.orderId())
                    .orElseThrow(() -> new OrderNotFoundException("Could not found the order while updating."));

            if (input.status() != null) {
                order.setStatus(input.status());
                log.info("Order {} status updated to {}", input.orderId(), input.status());
                if (input.status() == OrderStatus.COMPLETED) {
                    metricsService.incrementOrderCompleted();
                }
            }

            if (input.serviceDescription() != null) {
                order.setDescription(input.serviceDescription());
            }

            if (input.appliances() != null) {
                for (UpdateOrderItemDto dto : input.appliances()) {
                    if (dto.id() != null) {
                        updateOrderItemPort.execute(new UpdateOrderItemPort.Input(
                                dto.id(),
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

            if (input.parts() != null) {
                for (PartDto p : input.parts()) {
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
                        createDemandPort.execute(new CreateDemandPort.Input(
                                p.componentId(),
                                p.quantity().longValue(),
                                order.getId()
                        ));
                    }
                }
            }

            orderGateway.save(order);
            log.info("Order {} updated successfully", input.orderId());

            return new Output(true, "Order updated successfully");
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
