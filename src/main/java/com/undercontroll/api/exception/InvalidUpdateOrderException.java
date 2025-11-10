package com.undercontroll.api.exception;

public class InvalidUpdateOrderException extends RuntimeException {
    public InvalidUpdateOrderException(String message) {
        super(message);
    }
}
