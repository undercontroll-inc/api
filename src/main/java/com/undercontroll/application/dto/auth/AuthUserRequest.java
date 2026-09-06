package com.undercontroll.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User authentication request")
public record AuthUserRequest(

        @Schema(
                description = "User email",
                example = "admin@undercontroll.com",
                format = "email"
        )
        @NotBlank
        @Email
        String email,

        @Schema(
                description = "User password",
                example = "SecurePassword123!",
                format = "password"
        )
        @NotBlank
        String password
) {
}
