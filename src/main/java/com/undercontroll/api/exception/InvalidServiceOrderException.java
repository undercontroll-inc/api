package com.undercontroll.api.exception;

public class InvalidServiceOrderException extends RuntimeException {
    public InvalidServiceOrderException(String message) {
        super(message);
    }
}
