package com.undercontroll.infrastructure.mapper;

import com.undercontroll.domain.model.OrderItem;
import com.undercontroll.infrastructure.persistence.entity.OrderItemJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderItemMapper {

    OrderItem toDomain(OrderItemJpaEntity entity);

    List<OrderItem> toDomainList(List<OrderItemJpaEntity> entities);

    @Named("toEntity")
    @Mapping(target = "id", ignore = true)
    OrderItemJpaEntity toEntity(OrderItem domain);

    @Named("toEntityWithId")
    OrderItemJpaEntity toEntityWithId(OrderItem domain);
}
