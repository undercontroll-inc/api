package com.undercontroll.domain.exception;

public class InvalidUpdateComponentException extends RuntimeException {
    public static final String CODE = "COMPONENT_UPDATE_INVALID";

    public InvalidUpdateComponentException(String message) {
        super(message);
    }
}
