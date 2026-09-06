package com.undercontroll.domain.exception;

public class InvalidUserException extends RuntimeException {
    public static final String CODE = "INVALID_USER";

    public InvalidUserException(String message) {
        super(message);
    }
}
