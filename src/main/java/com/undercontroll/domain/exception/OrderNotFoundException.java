package com.undercontroll.domain.exception;

public class OrderNotFoundException extends RuntimeException {
    public static final String CODE = "ORDER_NOT_FOUND";

    public OrderNotFoundException(String message) {
        super(message);
    }
}
