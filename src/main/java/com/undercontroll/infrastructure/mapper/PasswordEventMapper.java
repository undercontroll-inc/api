package com.undercontroll.infrastructure.mapper;

import com.undercontroll.domain.model.PasswordEvent;
import com.undercontroll.infrastructure.persistence.entity.PasswordEventJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PasswordEventMapper {

    PasswordEvent toDomain(PasswordEventJpaEntity entity);

    List<PasswordEvent> toDomainList(List<PasswordEventJpaEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PasswordEventJpaEntity toEntity(PasswordEvent domain);

    PasswordEventJpaEntity toEntityWithId(PasswordEvent domain);
}
