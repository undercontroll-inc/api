package com.undercontroll.domain.exception;

public class TemplateLoadException extends RuntimeException {

    public static final String CODE = "ORDER_EXPORT_TEMPLATE_FAILED";

    public TemplateLoadException(String message) {
        super(message);
    }

    public TemplateLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

