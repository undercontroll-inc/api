package com.undercontroll.api.dto;

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
