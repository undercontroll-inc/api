package com.undercontroll.domain.exception;

public class InvalidComponentCreationException extends RuntimeException {
    public static final String CODE = "COMPONENT_CREATE_INVALID";

    public InvalidComponentCreationException(String message) {
        super(message);
    }
}
