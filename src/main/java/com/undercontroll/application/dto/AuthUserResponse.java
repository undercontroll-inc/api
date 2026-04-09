package com.undercontroll.application.dto;

public record AuthUserResponse(
        String token,
        String refreshToken,
        UserDto user
) {
}
