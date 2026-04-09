package com.undercontroll.infrastructure.mapper;

import com.undercontroll.domain.model.ComponentPart;
import com.undercontroll.infrastructure.persistence.entity.ComponentPartJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ComponentPartMapper {

    @Mapping(target = "demands", ignore = true)
    ComponentPart toDomain(ComponentPartJpaEntity entity);

    List<ComponentPart> toDomainList(List<ComponentPartJpaEntity> entities);

    @Named("toEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "demands", ignore = true)
    ComponentPartJpaEntity toEntity(ComponentPart domain);

    @Named("toEntityWithId")
    @Mapping(target = "demands", ignore = true)
    ComponentPartJpaEntity toEntityWithId(ComponentPart domain);
}
