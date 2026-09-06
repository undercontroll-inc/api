package com.undercontroll.domain.exception;

public class AnnouncementNotFoundException extends RuntimeException {
    public static final String CODE = "ANNOUNCEMENT_NOT_FOUND";

    public AnnouncementNotFoundException(String message) {
        super(message);
    }
}
