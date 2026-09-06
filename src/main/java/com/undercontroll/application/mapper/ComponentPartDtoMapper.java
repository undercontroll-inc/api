package com.undercontroll.application.mapper;

import com.undercontroll.application.dto.component.ComponentDto;
import com.undercontroll.domain.model.ComponentPart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ComponentPartDtoMapper {

    @Mapping(source = "name", target = "item")
    ComponentDto toDto(ComponentPart componentPart);

}
