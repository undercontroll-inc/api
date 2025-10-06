package com.undercontroll.api.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthUserRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
}
