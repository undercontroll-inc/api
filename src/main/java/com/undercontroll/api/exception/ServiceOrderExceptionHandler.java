package com.undercontroll.api.exception;

import com.undercontroll.api.exception.InvalidServiceOrderException;
import com.undercontroll.api.exception.ServiceOrderNotFoundException;
import com.undercontroll.api.dto.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ServiceOrderExceptionHandler extends GenericExceptionHandler {
    @ExceptionHandler(ServiceOrderNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleServiceOrderNotFound(
            ServiceOrderNotFoundException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidServiceOrderException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidServiceOrder(
            InvalidServiceOrderException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

}
