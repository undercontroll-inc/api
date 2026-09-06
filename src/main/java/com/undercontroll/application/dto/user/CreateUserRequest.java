package com.undercontroll.application.dto.user;

import com.undercontroll.domain.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to create a new user")
public record CreateUserRequest(
        @Schema(description = "User first name", example = "John")
        String name,

        @Schema(description = "Unique user email", example = "john.silva@email.com", format = "email")
        String email,

        @Schema(description = "User phone number", example = "11987654321")
        String phone,

        @Schema(description = "User last name", example = "Silva")
        String lastName,

        @Schema(description = "User password", example = "SecurePassword123!", format = "password")
        String password,

        @Schema(description = "Full address", example = "123 Flower Street - Sao Paulo/SP")
        String address,

        @Schema(description = "User tax id (digits only)", example = "12345678900")
        String cpf,

        @Schema(description = "Profile picture URL", example = "https://example.com/avatar.jpg")
        String avatarUrl,

        @Schema(description = "User type", example = "CUSTOMER", allowableValues = {"CUSTOMER", "ADMINISTRATOR"})
        UserType userType,

        @Schema(description = "Whether the user has WhatsApp", example = "true")
        Boolean hasWhatsApp,

        @Schema(description = "Whether the user is a recurring customer", example = "false")
        Boolean alreadyRecurrent,

        @Schema(description = "Whether this is the user's first login", example = "true")
        Boolean inFirstLogin,

        @Schema(description = "Postal code", example = "01234-567")
        String CEP
){
}
