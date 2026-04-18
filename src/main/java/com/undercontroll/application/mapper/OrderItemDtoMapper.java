package com.undercontroll.application.mapper;

import com.undercontroll.application.dto.OrderItemDto;
import com.undercontroll.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderItemDtoMapper {

    OrderItemDto toDto(OrderItem orderItem);

}
