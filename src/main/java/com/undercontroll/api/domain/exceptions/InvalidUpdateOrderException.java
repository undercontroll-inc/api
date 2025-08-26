package com.undercontroll.api.domain.exceptions;

public class InvalidUpdateOrderException extends RuntimeException {
    public InvalidUpdateOrderException(String message) {
        super(message);
    }
}
