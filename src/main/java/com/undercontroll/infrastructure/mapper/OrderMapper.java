package com.undercontroll.infrastructure.mapper;

import com.undercontroll.domain.model.Order;
import com.undercontroll.infrastructure.persistence.entity.OrderJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderItemMapper.class, DemandMapper.class, UserMapper.class})
public interface OrderMapper {

    Order toDomain(OrderJpaEntity entity);

    @Named("toDomainWithoutDemands")
    @Mapping(target = "demands", ignore = true)
    Order toDomainWithoutDemands(OrderJpaEntity entity);

    List<Order> toDomainList(List<OrderJpaEntity> entities);

    @Named("toEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", source = "orderItems", qualifiedByName = "toEntity")
    @Mapping(target = "demands", source = "demands", qualifiedByName = "toEntity")
    @Mapping(target = "user", source = "user", qualifiedByName = "toEntity")
    OrderJpaEntity toEntity(Order domain);

    @Named("toEntityWithId")
    @Mapping(target = "orderItems", source = "orderItems", qualifiedByName = "toEntityWithId")
    @Mapping(target = "demands", source = "demands", qualifiedByName = "toEntityWithId")
    @Mapping(target = "user", source = "user", qualifiedByName = "toEntityWithId")
    OrderJpaEntity toEntityWithId(Order domain);
}
