package com.undercontroll.domain.usecase.auth.impl;

import com.undercontroll.domain.usecase.auth.AuthUserPort;
import com.undercontroll.domain.exception.InvalidAuthException;
import com.undercontroll.domain.model.User;
import com.undercontroll.domain.gateway.UserGateway;
import com.undercontroll.infrastructure.service.TokenServce;
import com.undercontroll.infrastructure.service.RefreshTokenService;
import com.undercontroll.infrastructure.service.MetricsService;
import com.undercontroll.application.dto.UserDto;
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

    @Override
    public Output execute(Input input) {
        try {
            Optional<User> userFound = userGateway.findByEmail(input.email());

            if (userFound.isEmpty()) {
                metricsService.incrementLoginFailed();
                throw new InvalidAuthException("Email or password is invalid");
            }

            User user = userFound.get();

            // Google auth passes null password — skip password check
            if (input.password() != null) {
                boolean passwordMatch = passwordEncoder.matches(input.password(), user.getPassword());
                if (!passwordMatch) {
                    metricsService.incrementLoginFailed();
                    throw new InvalidAuthException("Email or password is invalid");
                }
            }

            String accessToken = tokenServce.generateToken(String.valueOf(user.getId()), user.getUserType());
            String refreshToken = refreshTokenService.createRefreshToken(user.getId(), user.getEmail(), user.getUserType());

            metricsService.incrementLoginSuccess();

            return new Output(accessToken, refreshToken, mapToDto(user));
        } catch (InvalidAuthException e) {
            throw e;
        }
    }

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLastName(),
                user.getAddress(),
                user.getCpf(),
                user.getCEP(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getHasWhatsApp(),
                user.getAlreadyRecurrent(),
                user.getInFirstLogin(),
                user.getUserType()
        );
    }
}
