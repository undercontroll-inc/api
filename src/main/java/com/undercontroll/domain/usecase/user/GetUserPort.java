package com.undercontroll.domain.usecase.user;

import com.undercontroll.application.dto.user.UserDto;

import java.util.Optional;

public interface GetUserPort {
    Optional<UserDto> execute(Integer userId);
}
