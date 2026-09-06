package com.undercontroll.domain.exception;

public class InvalidAuthException extends RuntimeException {
    public static final String CODE = "INVALID_CREDENTIALS";

    public InvalidAuthException(String message) {
        super(message);
    }
}
