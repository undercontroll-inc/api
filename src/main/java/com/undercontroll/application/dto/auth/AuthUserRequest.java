package com.undercontroll.application.dto.auth;

import com.undercontroll.domain.enums.AuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Sign-in request. Use provider to choose password or Google.")
public record AuthUserRequest(

        @Schema(description = "How the user is signing in", example = "PASSWORD")
        @NotNull
        AuthProvider provider,

        @Schema(
                description = "User email",
                example = "admin@undercontroll.com",
                format = "email"
        )
        @NotBlank
        @Email
        String email,

        @Schema(
                description = "User password. Required when provider is PASSWORD.",
                example = "SecurePassword123!",
                format = "password"
        )
        String password,

        @Schema(
                description = "Google ID token. Required when provider is GOOGLE.",
                example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String token
) {
}
