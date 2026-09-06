package com.undercontroll.domain.usecase.user;

import com.undercontroll.application.dto.user.CreateUserRequest;
import com.undercontroll.application.dto.user.CreateUserResponse;

public interface CreateUserPort {
    CreateUserResponse execute(CreateUserRequest request);
}
