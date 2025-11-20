package com.undercontroll.api.handlers;

import com.undercontroll.api.dto.ExceptionHandlerResponse;
import com.undercontroll.api.exception.InvalidDemandException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DemandExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(InvalidDemandException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidDemandException(
            InvalidDemandException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

}

