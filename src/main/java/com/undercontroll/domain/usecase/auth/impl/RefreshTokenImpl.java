package com.undercontroll.domain.usecase.auth.impl;

import com.undercontroll.application.dto.auth.RefreshTokenRequest;
import com.undercontroll.application.dto.auth.RefreshTokenResponse;
import com.undercontroll.domain.usecase.auth.RefreshTokenPort;
import com.undercontroll.domain.enums.UserType;
import com.undercontroll.infrastructure.service.RefreshTokenService;
import com.undercontroll.infrastructure.service.RefreshTokenService.RefreshTokenData;
import com.undercontroll.infrastructure.service.TokenServce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenImpl implements RefreshTokenPort {

    private final RefreshTokenService refreshTokenService;
    private final TokenServce tokenServce;

    @Override
    @Transactional
    public RefreshTokenResponse execute(RefreshTokenRequest request) {
        RefreshTokenData data = refreshTokenService.consumeRefreshToken(request.refreshToken());

        UserType userType = UserType.valueOf(data.userRole());

        String newRefreshToken = refreshTokenService.createRefreshToken(
                data.userId(), data.userEmail(), userType
        );

        String newAccessToken = tokenServce.generateToken(String.valueOf(data.userId()), userType);
        log.info("Refresh token rotated userId={}", data.userId());
        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }
}
