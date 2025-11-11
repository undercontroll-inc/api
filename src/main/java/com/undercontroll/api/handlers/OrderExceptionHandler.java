package com.undercontroll.api.handlers;

import com.undercontroll.api.exception.OrderNotFoundException;
import com.undercontroll.api.dto.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleDuplicatedWallet(
            OrderNotFoundException ex, HttpServletRequest request
    ) {

        return this.buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

}
