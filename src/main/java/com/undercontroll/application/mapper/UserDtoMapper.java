package com.undercontroll.application.mapper;

import com.undercontroll.application.dto.UserDto;
import com.undercontroll.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserDtoMapper {
    
    UserDto toDto(User user);
    
    User toDomain(UserDto userDto);
}
