package com.undercontroll.domain.exception;

public class AnaUnavailableException extends RuntimeException {

    public static final String CODE = "CHAT_UNAVAILABLE";
    public static final String MESSAGE = "Ana is currently unavailable.";

    public AnaUnavailableException() {
        super(MESSAGE);
    }

    public AnaUnavailableException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
