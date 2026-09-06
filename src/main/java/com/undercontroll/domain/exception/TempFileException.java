package com.undercontroll.domain.exception;

public class TempFileException extends RuntimeException {

    public static final String CODE = "ORDER_EXPORT_TEMP_FILE_FAILED";

    public TempFileException(String message) {
        super(message);
    }

    public TempFileException(String message, Throwable cause) {
        super(message, cause);
    }
}

