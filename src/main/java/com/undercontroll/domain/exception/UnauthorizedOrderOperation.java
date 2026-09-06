package com.undercontroll.domain.exception;

public class UnauthorizedOrderOperation extends RuntimeException {
    public static final String CODE = "ORDER_UNAUTHORIZED";

    public UnauthorizedOrderOperation(String message) {
        super(message);
    }
}
