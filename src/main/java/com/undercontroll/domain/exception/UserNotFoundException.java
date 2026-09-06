package com.undercontroll.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public static final String CODE = "USER_NOT_FOUND";

    public UserNotFoundException(String message) {
        super(message);
    }
}
