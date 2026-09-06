package com.undercontroll.domain.usecase.auth.impl;

import com.undercontroll.application.dto.auth.AuthUserRequest;
import com.undercontroll.application.dto.auth.AuthUserResponse;
import com.undercontroll.application.mapper.UserDtoMapper;
import com.undercontroll.domain.enums.AuthProvider;
import com.undercontroll.domain.exception.InvalidAuthException;
import com.undercontroll.domain.gateway.UserGateway;
import com.undercontroll.domain.model.User;
import com.undercontroll.domain.usecase.auth.AuthUserPort;
import com.undercontroll.infrastructure.service.GoogleAuthService;
import com.undercontroll.infrastructure.service.MetricsService;
import com.undercontroll.infrastructure.service.RefreshTokenService;
import com.undercontroll.infrastructure.service.TokenServce;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUserImpl implements AuthUserPort {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;
    private final TokenServce tokenServce;
    private final RefreshTokenService refreshTokenService;
    private final MetricsService metricsService;
    private final UserDtoMapper userDtoMapper;
    private final GoogleAuthService googleAuthService;

    @Override
    public AuthUserResponse execute(AuthUserRequest request) {
        Optional<User> userFound = userGateway.findByEmail(request.email());
        if (userFound.isEmpty()) {
            return fail();
        }

        User user = userFound.get();
        if (request.provider() == null || !isAuthenticated(request, user)) {
            return fail();
        }

        String accessToken = tokenServce.generateToken(String.valueOf(user.getId()), user.getUserType());
        String refreshToken = refreshTokenService.createRefreshToken(user.getId(), user.getEmail(), user.getUserType());
        metricsService.incrementLoginSuccess();
        return new AuthUserResponse(accessToken, refreshToken, userDtoMapper.toDto(user));
    }

    private boolean isAuthenticated(AuthUserRequest request, User user) {
        return switch (request.provider()) {
            case PASSWORD -> request.password() != null
                    && !request.password().isBlank()
                    && passwordEncoder.matches(request.password(), user.getPassword());
            case GOOGLE -> request.token() != null
                    && !request.token().isBlank()
                    && googleAuthService.verify(request.token(), request.email());
        };
    }

    private AuthUserResponse fail() {
        metricsService.incrementLoginFailed();
        throw new InvalidAuthException("Email or password is invalid");
    }
}
