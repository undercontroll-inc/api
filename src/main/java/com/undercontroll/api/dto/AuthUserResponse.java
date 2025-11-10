package com.undercontroll.api.dto;

public record AuthUserResponse(
        String token,
        UserDto user
) {
}
