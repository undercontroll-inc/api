package com.undercontroll.domain.exception;

public class PdfGenerationException extends RuntimeException {

    public static final String CODE = "ORDER_EXPORT_PDF_FAILED";

    public PdfGenerationException(String message) {
        super(message);
    }

    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
