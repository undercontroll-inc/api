package com.undercontroll.infrastructure.handler;

import com.undercontroll.domain.exception.*;
import com.undercontroll.application.dto.common.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidUser(
            InvalidUserException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), InvalidUserException.CODE
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), UserNotFoundException.CODE
        );
    }

    @ExceptionHandler(InvalidPasswordResetException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidPasswordResetException(
            InvalidPasswordResetException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), InvalidPasswordResetException.CODE
        );
    }

}
