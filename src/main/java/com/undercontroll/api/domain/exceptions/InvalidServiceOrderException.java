package com.undercontroll.api.domain.exceptions;

public class InvalidServiceOrderException extends RuntimeException {
    public InvalidServiceOrderException(String message) {
        super(message);
    }
}
