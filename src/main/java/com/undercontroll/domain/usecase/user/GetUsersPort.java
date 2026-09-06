package com.undercontroll.domain.usecase.user;

import com.undercontroll.application.dto.user.UserDto;
import com.undercontroll.domain.enums.UserType;

import java.util.List;

public interface GetUsersPort {
    List<UserDto> execute(UserType type, Boolean hasEmail);
}
