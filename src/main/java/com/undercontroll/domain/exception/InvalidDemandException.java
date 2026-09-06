package com.undercontroll.domain.exception;

public class InvalidDemandException extends RuntimeException {
    public static final String CODE = "DEMAND_INVALID";

    public InvalidDemandException(String message) {
        super(message);
    }
}
