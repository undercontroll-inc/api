package com.undercontroll.api.domain.exceptions;

public class InvalidDeleteOrderException extends RuntimeException {
    public InvalidDeleteOrderException(String message) {
        super(message);
    }
}
