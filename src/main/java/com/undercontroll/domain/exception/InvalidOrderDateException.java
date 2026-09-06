package com.undercontroll.domain.exception;

public class InvalidOrderDateException extends RuntimeException {
    public static final String CODE = "ORDER_INVALID_DATE";

    public InvalidOrderDateException(String message) {
        super(message);
    }
}
