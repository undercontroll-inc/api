package com.undercontroll.application.dto.auth;

public record AuthGoogleRequest(
        String email,
        String token
) {
}
