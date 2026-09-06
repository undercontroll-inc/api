package com.undercontroll.domain.exception;

public class ComponentNotFoundException extends RuntimeException {
    public static final String CODE = "COMPONENT_NOT_FOUND";

    public ComponentNotFoundException(String message) {
        super(message);
    }
}
