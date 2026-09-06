package com.undercontroll.application.dto.auth;

public record ResetPasswordRequest(
        String newPassword,
        boolean inFirstLogin
) {
}
