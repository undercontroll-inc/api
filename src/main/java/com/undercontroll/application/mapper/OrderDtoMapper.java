package com.undercontroll.application.mapper;

import com.undercontroll.application.dto.order.OrderEnrichedDto;
import com.undercontroll.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderDtoMapper {

    private final UserDtoMapper userDtoMapper;
    private final OrderItemDtoMapper orderItemDtoMapper;
    private final ComponentPartDtoMapper componentDtoMapper;

    public OrderEnrichedDto toEnrichedDto(Order o, boolean includeTechnical) {
        return new OrderEnrichedDto(
                o.getId(),
                this.userDtoMapper.toDto(o.getUser()),
                o.getOrderItems().stream().map(orderItemDtoMapper::toDto).toList(),
                o.getDemands().stream().map(d -> this.componentDtoMapper.toDto(d.getComponent())).toList(),
                o.calculatePartsTotal(),
                o.calculateLaborTotal(),
                o.getDiscount(),
                o.getTotal(),
                o.getReceived_at() != null ? o.getReceived_at().toString() : null,
                null,
                o.getNf(),
                o.isReturnGuarantee(),
                o.getCustomerDescription(),
                includeTechnical ? o.getTechnicalDescription() : null,
                o.getStatus(),
                o.getUpdatedAt() != null ? o.getUpdatedAt().toString() : null
        );
    }

}
