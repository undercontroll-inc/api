package com.undercontroll.infrastructure.mapper;

import com.undercontroll.domain.model.User;
import com.undercontroll.infrastructure.persistence.entity.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toDomain(UserJpaEntity entity);

    List<User> toDomainList(List<UserJpaEntity> entities);

    @Named("toEntity")
    @Mapping(target = "id", ignore = true)
    UserJpaEntity toEntity(User domain);

    @Named("toEntityWithId")
    UserJpaEntity toEntityWithId(User domain);
}
