package com.undercontroll.domain.exception;

public class InvalidTranscriptionException extends RuntimeException {

    public static final String CODE = "TRANSCRIPTION_INVALID";

    public InvalidTranscriptionException(String message) {
        super(message);
    }
}
