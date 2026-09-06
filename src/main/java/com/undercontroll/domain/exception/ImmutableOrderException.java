package com.undercontroll.domain.exception;

public class ImmutableOrderException extends RuntimeException {
    public static final String CODE = "ORDER_IMMUTABLE";

    public ImmutableOrderException(String message) {
        super(message);
    }
}

