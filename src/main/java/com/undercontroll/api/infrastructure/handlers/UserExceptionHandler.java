package com.undercontroll.api.infrastructure.handlers;

import com.undercontroll.api.domain.exceptions.InvalidUserException;
import com.undercontroll.api.domain.exceptions.UserNotFoundException;
import com.undercontroll.api.infrastructure.handlers.dto.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler  extends GenericExceptionHandler{

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidUser(
            InvalidUserException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }
}
