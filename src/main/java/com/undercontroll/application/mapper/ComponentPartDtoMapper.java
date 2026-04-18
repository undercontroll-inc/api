package com.undercontroll.application.mapper;

import com.undercontroll.application.dto.ComponentDto;
import com.undercontroll.domain.model.ComponentPart;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ComponentPartDtoMapper {

    ComponentDto toDto(ComponentPart componentPart);

}
