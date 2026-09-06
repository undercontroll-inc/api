package com.undercontroll.domain.exception;

public class TokenGenerationException extends RuntimeException {
    public static final String CODE = "TOKEN_GENERATION_FAILED";

    public TokenGenerationException(String message) {
        super(message);
    }
}
