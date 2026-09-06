package com.undercontroll.domain.exception;

public class InvalidUpdateOrderException extends RuntimeException {
    public static final String CODE = "ORDER_UPDATE_INVALID";

    public InvalidUpdateOrderException(String message) {
        super(message);
    }
}
