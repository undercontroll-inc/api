package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.domain.usecase.order.CreateOrderPort;
import com.undercontroll.domain.usecase.order_item.CreateOrderItemPort;
import com.undercontroll.domain.usecase.demand.CreateDemandPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.model.ComponentPart;
import com.undercontroll.domain.model.User;
import com.undercontroll.domain.enums.OrderStatus;
import com.undercontroll.domain.gateway.OrderGateway;
import com.undercontroll.domain.gateway.UserGateway;
import com.undercontroll.domain.gateway.StockManagementGateway;
import com.undercontroll.infrastructure.service.MetricsService;
import com.undercontroll.application.dto.PartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderImpl implements CreateOrderPort {

    private final OrderGateway orderGateway;
    private final UserGateway userGateway;
    private final StockManagementGateway stockManagementGateway;
    private final CreateOrderItemPort createOrderItemPort;
    private final CreateDemandPort createDemandPort;
    private final MetricsService metricsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"orders", "ordersByUser", "order", "orderParts", "dashboardMetrics"}, allEntries = true)
    public Output execute(Input input) {
        long startTime = System.currentTimeMillis();
        log.info("Creating new order for user {}", input.userId());

        LocalDate completedFormatted = formatOrderDate(input.deadline());
        LocalDate receivedFormatted = formatOrderDate(input.receivedAt());

        Map<Integer, ComponentPart> validatedComponents = new HashMap<>();
        double partsTotal = 0.0;

        for (PartDto part : input.parts()) {
            ComponentPart component = stockManagementGateway.findComponentById(part.componentId())
                    .orElseThrow(() -> new RuntimeException("Component not found"));
            stockManagementGateway.validateStockAvailability(component, part.quantity());

            validatedComponents.put(part.componentId(), component);
            partsTotal += part.quantity() * component.getPrice();
        }

        List<OrderItem> orderItems = new ArrayList<>();
        Double laborTotal = 0.0;

        for (var appliance : input.appliances()) {
            Double labor = appliance.laborValue() == null ? 0.0 : appliance.laborValue();

            var orderItemCreated = createOrderItemPort.execute(new CreateOrderItemPort.Input(
                    appliance.brand(),
                    appliance.model(),
                    appliance.type(),
                    "",
                    appliance.customerNote(),
                    appliance.voltage(),
                    appliance.serial(),
                    labor
            ));

            orderItems.add(OrderItem.builder()
                    .id(orderItemCreated.id())
                    .brand(orderItemCreated.brand())
                    .model(orderItemCreated.model())
                    .type(orderItemCreated.type())
                    .laborValue(orderItemCreated.laborValue())
                    .build());
            laborTotal += orderItemCreated.laborValue();
        }

        User user = userGateway.findById(input.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Double total = partsTotal + laborTotal - input.discount();

        Order order = Order.builder()
                .orderItems(orderItems)
                .status(OrderStatus.PENDING)
                .user(user)
                .discount(input.discount())
                .date(null)
                .store("Loja")
                .nf(input.nf())
                .fabricGuarantee(input.fabricGuarantee())
                .received_at(receivedFormatted)
                .description(input.serviceDescription())
                .returnGuarantee(input.returnGuarantee())
                .completedTime(completedFormatted)
                .total(total)
                .build();

        Order savedOrder = orderGateway.save(order);

        log.info("Order {} created successfully", savedOrder.getId());

        for (PartDto part : input.parts()) {
            ComponentPart component = validatedComponents.get(part.componentId());

            createDemandPort.execute(new CreateDemandPort.Input(
                    component.getId(),
                    Long.valueOf(part.quantity()),
                    savedOrder.getId()
            ));

            stockManagementGateway.decreaseStock(component.getId(), part.quantity());
        }

        metricsService.incrementOrderCreated();
        metricsService.recordOrderProcessingTime(startTime);

        log.info("Order {} created with {} demands", savedOrder.getId(), input.parts().size());

        return new Output(
                savedOrder.getId(),
                input.userId(),
                savedOrder.getStatus().toString(),
                savedOrder.getTotal()
        );
    }

    private LocalDate formatOrderDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return LocalDate.parse(dateStr);
        }
    }
}
