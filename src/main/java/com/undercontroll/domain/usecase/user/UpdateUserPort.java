package com.undercontroll.domain.usecase.user;

import com.undercontroll.application.dto.user.UpdateUserRequest;

public interface UpdateUserPort {
    void execute(Integer userId, UpdateUserRequest request);
}
