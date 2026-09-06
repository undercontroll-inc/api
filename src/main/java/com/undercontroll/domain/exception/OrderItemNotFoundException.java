package com.undercontroll.domain.exception;

public class OrderItemNotFoundException extends RuntimeException {
    public static final String CODE = "ORDER_ITEM_NOT_FOUND";

    public OrderItemNotFoundException(String message) {
        super(message);
    }
}