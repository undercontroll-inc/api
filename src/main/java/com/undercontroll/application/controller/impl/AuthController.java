package com.undercontroll.application.controller.impl;

import com.undercontroll.application.controller.AuthApi;
import com.undercontroll.application.dto.auth.AuthGoogleRequest;
import com.undercontroll.application.dto.auth.AuthUserRequest;
import com.undercontroll.application.dto.auth.AuthUserResponse;
import com.undercontroll.application.dto.auth.RefreshTokenRequest;
import com.undercontroll.application.dto.auth.RefreshTokenResponse;
import com.undercontroll.domain.usecase.auth.AuthUserPort;
import com.undercontroll.domain.usecase.auth.RefreshTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthUserPort authUserPort;
    private final RefreshTokenPort refreshTokenPort;

    @Override
    public ResponseEntity<AuthUserResponse> login(AuthUserRequest request) {
        return ResponseEntity.ok(authUserPort.execute(request));
    }

    @Override
    public ResponseEntity<AuthUserResponse> loginWithGoogle(AuthGoogleRequest request) {
        return ResponseEntity.ok(authUserPort.execute(new AuthUserRequest(request.email(), null)));
    }

    @Override
    public ResponseEntity<RefreshTokenResponse> refresh(RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenPort.execute(request));
    }
}
