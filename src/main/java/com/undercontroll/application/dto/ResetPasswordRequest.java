package com.undercontroll.application.dto;

public record ResetPasswordRequest(
        String newPassword,
        boolean inFirstLogin
) {
}
