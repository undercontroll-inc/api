package com.undercontroll.api.exception;

import com.undercontroll.api.exception.*;
import com.undercontroll.api.dto.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ComponentExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(InvalidComponentCreationException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidOrderCreationException(
            InvalidComponentCreationException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidGetComponentsByCategoryExcepiton.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidGetComponentsByCategoryException(
            InvalidGetComponentsByCategoryExcepiton ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidGetComponentByNameException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidGetComponentByNameException(
            InvalidGetComponentByNameException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidUpdateComponentException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidUpdateComponentException(
            InvalidUpdateComponentException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ComponentNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleComponentNotFoundException(
            ComponentNotFoundException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

}
