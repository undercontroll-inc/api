package com.undercontroll.infrastructure.mapper;

import com.undercontroll.domain.model.RefreshToken;
import com.undercontroll.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RefreshTokenMapper {

    RefreshToken toDomain(RefreshTokenJpaEntity entity);

    List<RefreshToken> toDomainList(List<RefreshTokenJpaEntity> entities);

    @Mapping(target = "id", ignore = true)
    RefreshTokenJpaEntity toEntity(RefreshToken domain);

    RefreshTokenJpaEntity toEntityWithId(RefreshToken domain);
}
