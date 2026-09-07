package com.undercontroll.domain.exception;

public class TranscriptionUnavailableException extends RuntimeException {

    public static final String CODE = "TRANSCRIPTION_UNAVAILABLE";
    public static final String MESSAGE = "Speech-to-text is currently unavailable.";

    public TranscriptionUnavailableException() {
        super(MESSAGE);
    }

    public TranscriptionUnavailableException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
