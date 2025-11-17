package com.undercontroll.api.exception;

public class UnauthorizedOrderOperation extends RuntimeException {
    public UnauthorizedOrderOperation(String message) {
        super(message);
    }
}
