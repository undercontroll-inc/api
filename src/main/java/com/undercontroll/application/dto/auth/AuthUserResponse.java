package com.undercontroll.application.dto.auth;

import com.undercontroll.application.dto.user.UserDto;

public record AuthUserResponse(
        String accessToken,
        String refreshToken,
        UserDto user
) {
}
