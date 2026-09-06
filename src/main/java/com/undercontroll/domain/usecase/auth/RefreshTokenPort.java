package com.undercontroll.domain.usecase.auth;

import com.undercontroll.application.dto.auth.RefreshTokenRequest;
import com.undercontroll.application.dto.auth.RefreshTokenResponse;

public interface RefreshTokenPort {
    RefreshTokenResponse execute(RefreshTokenRequest request);
}
