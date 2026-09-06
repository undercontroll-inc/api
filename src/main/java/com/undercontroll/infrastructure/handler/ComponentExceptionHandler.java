package com.undercontroll.infrastructure.handler;

import com.undercontroll.domain.exception.*;
import com.undercontroll.application.dto.common.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ComponentExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(InvalidComponentCreationException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidComponentCreationException(
            InvalidComponentCreationException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), InvalidComponentCreationException.CODE
        );
    }

    @ExceptionHandler(InvalidUpdateComponentException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidUpdateComponentException(
            InvalidUpdateComponentException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), InvalidUpdateComponentException.CODE
        );
    }

    @ExceptionHandler(ComponentNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleComponentNotFoundException(
            ComponentNotFoundException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), ComponentNotFoundException.CODE
        );
    }

    @ExceptionHandler(InvalidDeleteComponentException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidDeleteComponentException(
            InvalidDeleteComponentException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), InvalidDeleteComponentException.CODE
        );
    }

}
