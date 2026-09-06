package com.undercontroll.domain.exception;

public class InvalidDeleteComponentException extends RuntimeException {
    public static final String CODE = "COMPONENT_DELETE_INVALID";

    public InvalidDeleteComponentException(String message) {
        super(message);
    }
}
