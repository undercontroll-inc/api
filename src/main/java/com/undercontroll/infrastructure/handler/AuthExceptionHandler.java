package com.undercontroll.infrastructure.handler;

import com.undercontroll.application.dto.common.ExceptionHandlerResponse;
import com.undercontroll.domain.exception.InvalidAuthException;
import com.undercontroll.domain.exception.InvalidTokenException;
import com.undercontroll.domain.exception.TokenGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(InvalidAuthException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidAuth(
            InvalidAuthException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI(), InvalidAuthException.CODE
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidToken(
            InvalidTokenException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI(), ex.getErrorCode()
        );
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleTokenGeneration(
            TokenGenerationException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getRequestURI(),
                TokenGenerationException.CODE,
                ex
        );
    }

}
