package com.undercontroll.domain.exception;

public class InvalidOrderItemException extends RuntimeException {
    public static final String CODE = "ORDER_ITEM_INVALID";

    public InvalidOrderItemException(String message) {
        super(message);
    }
}