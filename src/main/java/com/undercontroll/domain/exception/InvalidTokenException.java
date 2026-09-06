package com.undercontroll.domain.exception;

public class InvalidTokenException extends RuntimeException {
    public static final String CODE = "INVALID_TOKEN";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String REFRESH_TOKEN_REUSED = "REFRESH_TOKEN_REUSED";
    public static final String UNAUTHENTICATED = "UNAUTHENTICATED";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";

    private final String code;

    public InvalidTokenException(String message) {
        this(message, CODE);
    }

    public InvalidTokenException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
