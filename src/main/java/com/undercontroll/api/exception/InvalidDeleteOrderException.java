package com.undercontroll.api.exception;

public class InvalidDeleteOrderException extends RuntimeException {
    public InvalidDeleteOrderException(String message) {
        super(message);
    }
}
