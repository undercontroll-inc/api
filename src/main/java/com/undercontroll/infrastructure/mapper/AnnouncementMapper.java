package com.undercontroll.infrastructure.mapper;

import com.undercontroll.domain.model.Announcement;
import com.undercontroll.infrastructure.persistence.entity.AnnouncementJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AnnouncementMapper {

    Announcement toDomain(AnnouncementJpaEntity entity);

    List<Announcement> toDomainList(List<AnnouncementJpaEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AnnouncementJpaEntity toEntity(Announcement domain);

    AnnouncementJpaEntity toEntityWithId(Announcement domain);
}
