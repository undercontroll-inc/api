package com.undercontroll.domain.usecase.auth;

import com.undercontroll.application.dto.auth.AuthUserRequest;
import com.undercontroll.application.dto.auth.AuthUserResponse;

public interface AuthUserPort {
    AuthUserResponse execute(AuthUserRequest request);
}
