package com.undercontroll.domain.usecase.auth.impl;

import com.undercontroll.application.dto.auth.AuthUserRequest;
import com.undercontroll.application.dto.auth.AuthUserResponse;
import com.undercontroll.application.mapper.UserDtoMapper;
import com.undercontroll.domain.usecase.auth.AuthUserPort;
import com.undercontroll.domain.exception.InvalidAuthException;
import com.undercontroll.domain.model.User;
import com.undercontroll.domain.gateway.UserGateway;
import com.undercontroll.infrastructure.service.TokenServce;
import com.undercontroll.infrastructure.service.RefreshTokenService;
import com.undercontroll.infrastructure.service.MetricsService;
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

    @Override
    public AuthUserResponse execute(AuthUserRequest request) {
        try {
            Optional<User> userFound = userGateway.findByEmail(request.email());

            if (userFound.isEmpty()) {
                metricsService.incrementLoginFailed();
                throw new InvalidAuthException("Email or password is invalid");
            }

            User user = userFound.get();

            // Google auth passes null password — skip password check
            if (request.password() != null) {
                boolean passwordMatch = passwordEncoder.matches(request.password(), user.getPassword());
                if (!passwordMatch) {
                    metricsService.incrementLoginFailed();
                    throw new InvalidAuthException("Email or password is invalid");
                }
            }

            String accessToken = tokenServce.generateToken(String.valueOf(user.getId()), user.getUserType());
            String refreshToken = refreshTokenService.createRefreshToken(user.getId(), user.getEmail(), user.getUserType());

            metricsService.incrementLoginSuccess();

            return new AuthUserResponse(accessToken, refreshToken, userDtoMapper.toDto(user));
        } catch (InvalidAuthException e) {
            throw e;
        }
    }
}
