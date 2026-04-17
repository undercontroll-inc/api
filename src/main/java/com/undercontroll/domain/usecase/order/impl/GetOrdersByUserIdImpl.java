package com.undercontroll.domain.usecase.order.impl;

import com.undercontroll.application.dto.*;
import com.undercontroll.domain.model.ComponentPart;
import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.domain.model.User;
import com.undercontroll.domain.usecase.order.GetOrdersByUserIdPort;
import com.undercontroll.domain.model.Order;
import com.undercontroll.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrdersByUserIdImpl implements GetOrdersByUserIdPort {

    private final OrderGateway orderGateway;

    @Override
    public Output execute(Input input) {
        List<Order> orders = orderGateway.findByUserId(input.userId());


        List<OrderEnrichedDto> orderDtos = orders.stream()
                .map(o -> new OrderEnrichedDto(
                        o.getId(),
                        this.mapUser(o.getUser()),
                        o.getOrderItems().stream().map(this::mapOrderItem).toList(),
                        o.getDemands().stream().map(d -> this.mapComponent(d.getComponent())).toList(),
                        o.calculatePartsTotal(),
                        o.calculateLaborTotal(),
                        o.getDiscount(),
                        o.getTotal(),
                        o.getReceived_at() != null ? o.getReceived_at().toString() : null,
                        null,
                        o.getNf(),
                        o.isReturnGuarantee(),
                        o.getDescription(),
                        null,
                        o.getStatus(),
                        o.getUpdatedAt() != null ? o.getUpdatedAt().toString() : null
                ))
                .toList();
        return new Output(new GetOrdersByUserIdResponse(orderDtos));
    }

    private UserDto mapUser(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLastName(),
                user.getAddress(),
                user.getCpf(),
                user.getCEP(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getHasWhatsApp(),
                user.getAlreadyRecurrent(),
                user.getInFirstLogin(),
                user.getUserType()
        );
    }

    private OrderItemDto mapOrderItem(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getImageUrl(),
                orderItem.getModel(),
                orderItem.getType(),
                orderItem.getBrand(),
                orderItem.getObservation(),
                orderItem.getVolt(),
                orderItem.getSeries(),
                orderItem.getLaborValue(),
               orderItem.getCompletedAt()
        );
    }

    private ComponentDto mapComponent(ComponentPart c) {
        return new ComponentDto(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getBrand(),
                c.getPrice(),
                c.getQuantity(),
                c.getSupplier(),
                c.getCategory()
        );
    }

}
