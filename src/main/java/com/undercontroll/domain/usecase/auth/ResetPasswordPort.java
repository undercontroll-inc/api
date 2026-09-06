package com.undercontroll.domain.usecase.auth;

import com.undercontroll.application.dto.auth.ResetPasswordRequest;

public interface ResetPasswordPort {
    void execute(Integer userId, ResetPasswordRequest request, String token);
}
