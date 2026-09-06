package com.undercontroll.domain.exception;

public class InsuficientComponentException extends RuntimeException {
    public static final String CODE = "ORDER_INSUFFICIENT_COMPONENT_STOCK";

    public InsuficientComponentException(String message) {
        super(message);
    }
}
