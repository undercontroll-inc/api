package com.undercontroll.domain.exception;

public class InvalidPasswordResetException extends RuntimeException {
    public static final String CODE = "USER_PASSWORD_RESET_INVALID";

    public InvalidPasswordResetException(String message) {
        super(message);
    }
}
