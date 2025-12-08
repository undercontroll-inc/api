package com.undercontroll.api.dto;

public record ResetPasswordRequest(
        String newPassword,
        boolean inFirstLogin
) {
}
