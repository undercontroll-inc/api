package com.undercontroll.domain.exception;

public class InvalidDeleteOrderException extends RuntimeException {
    public static final String CODE = "ORDER_DELETE_INVALID";

    public InvalidDeleteOrderException(String message) {
        super(message);
    }
}
