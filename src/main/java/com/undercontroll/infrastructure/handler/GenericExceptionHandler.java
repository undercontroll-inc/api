package com.undercontroll.infrastructure.handler;

import com.undercontroll.application.dto.common.ExceptionHandlerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public abstract class GenericExceptionHandler {

    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    protected ResponseEntity<ExceptionHandlerResponse> buildErrorResponse(HttpStatus status, String message, String path) {
        return buildErrorResponse(status, message, path, null, null);
    }

    protected ResponseEntity<ExceptionHandlerResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            String path,
            String code
    ) {
        return buildErrorResponse(status, message, path, code, null);
    }

    protected ResponseEntity<ExceptionHandlerResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            String path,
            String code,
            List<ExceptionHandlerResponse.FieldError> errors
    ) {
        ExceptionHandlerResponse error = new ExceptionHandlerResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now(),
                code,
                errors);

        return new ResponseEntity<>(error, status);
    }

}
