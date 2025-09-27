package com.undercontroll.api.application.dto;

public record AuthUserRequest(
        String email,
        String password
) {
}
