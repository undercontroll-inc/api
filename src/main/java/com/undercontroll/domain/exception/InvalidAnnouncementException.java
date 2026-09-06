package com.undercontroll.domain.exception;

public class InvalidAnnouncementException extends RuntimeException {
    public static final String CODE = "ANNOUNCEMENT_INVALID";

    public InvalidAnnouncementException(String message) {
        super(message);
    }
}
