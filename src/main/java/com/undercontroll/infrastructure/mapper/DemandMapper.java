package com.undercontroll.infrastructure.mapper;

import com.undercontroll.domain.model.Demand;
import com.undercontroll.domain.model.Order;
import com.undercontroll.infrastructure.persistence.entity.DemandJpaEntity;
import com.undercontroll.infrastructure.persistence.entity.OrderJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ComponentPartMapper.class})
public interface DemandMapper {

    @Mapping(target = "order", source = "order", qualifiedByName = "toDomainOrderReference")
    Demand toDomain(DemandJpaEntity entity);

    List<Demand> toDomainList(List<DemandJpaEntity> entities);

    @Named("toEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", source = "order", qualifiedByName = "toEntityOrderReference")
    @Mapping(target = "component", source = "component", qualifiedByName = "toEntity")
    DemandJpaEntity toEntity(Demand domain);

    @Named("toEntityWithId")
    @Mapping(target = "order", source = "order", qualifiedByName = "toEntityOrderReference")
    @Mapping(target = "component", source = "component", qualifiedByName = "toEntityWithId")
    DemandJpaEntity toEntityWithId(Demand domain);

    @Named("toDomainOrderReference")
    default Order toDomainOrderReference(OrderJpaEntity order) {
        if (order == null) {
            return null;
        }

        return Order.builder()
                .id(order.getId())
                .build();
    }

    @Named("toEntityOrderReference")
    default OrderJpaEntity toEntityOrderReference(Order order) {
        if (order == null) {
            return null;
        }

        return OrderJpaEntity.builder()
                .id(order.getId())
                .build();
    }
}
