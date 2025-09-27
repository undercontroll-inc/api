package com.undercontroll.api.application.dto;

public record AuthUserResponse(
        String token,
        UserDto user
) {
}
