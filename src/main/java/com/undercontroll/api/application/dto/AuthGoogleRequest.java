package com.undercontroll.api.application.dto;

public record AuthGoogleRequest(
        String email,
        String token
) {
}
