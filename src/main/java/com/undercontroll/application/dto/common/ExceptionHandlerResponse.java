package com.undercontroll.application.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

public class ExceptionHandlerResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FieldError> errors;

    public ExceptionHandlerResponse() {
    }

    public ExceptionHandlerResponse(int status, String error, String message, String path, LocalDateTime timestamp) {
        this(status, error, message, path, timestamp, null, null);
    }

    public ExceptionHandlerResponse(
            int status,
            String error,
            String message,
            String path,
            LocalDateTime timestamp,
            String code
    ) {
        this(status, error, message, path, timestamp, code, null);
    }

    public ExceptionHandlerResponse(
            int status,
            String error,
            String message,
            String path,
            LocalDateTime timestamp,
            String code,
            List<FieldError> errors
    ) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.code = code;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    public record FieldError(String field, String message) {
    }
}
