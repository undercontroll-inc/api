package com.undercontroll.api.exception;

public class InvalidOrderDateException extends RuntimeException {
    public InvalidOrderDateException(String message) {
        super(message);
    }
}
