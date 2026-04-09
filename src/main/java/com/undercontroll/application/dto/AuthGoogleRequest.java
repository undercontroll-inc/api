package com.undercontroll.application.dto;

public record AuthGoogleRequest(
        String email,
        String token
) {
}
