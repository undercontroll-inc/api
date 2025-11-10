package com.undercontroll.api.dto;

public record AuthGoogleRequest(
        String email,
        String token
) {
}
