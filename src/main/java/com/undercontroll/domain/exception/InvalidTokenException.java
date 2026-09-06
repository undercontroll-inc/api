package com.undercontroll.domain.exception;

public class InvalidTokenException extends RuntimeException {
    public static final String CODE = "INVALID_TOKEN";

    public InvalidTokenException(String message) {
        super(message);
    }
}
