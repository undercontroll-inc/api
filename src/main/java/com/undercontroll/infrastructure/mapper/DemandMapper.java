package com.undercontroll.infrastructure.mapper;

import com.undercontroll.domain.model.Demand;
import com.undercontroll.infrastructure.persistence.entity.DemandJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ComponentPartMapper.class})
public interface DemandMapper {

    @Mapping(target = "order", ignore = true)
    Demand toDomain(DemandJpaEntity entity);

    List<Demand> toDomainList(List<DemandJpaEntity> entities);

    @Named("toEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "component", source = "component", qualifiedByName = "toEntity")
    DemandJpaEntity toEntity(Demand domain);

    @Named("toEntityWithId")
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "component", source = "component", qualifiedByName = "toEntityWithId")
    DemandJpaEntity toEntityWithId(Demand domain);
}
