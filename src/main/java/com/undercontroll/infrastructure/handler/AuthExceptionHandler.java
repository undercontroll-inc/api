package com.undercontroll.infrastructure.handler;

import com.undercontroll.domain.exception.InvalidAuthException;
import com.undercontroll.domain.exception.InvalidTokenException;
import com.undercontroll.domain.exception.TokenGenerationException;
import com.undercontroll.application.dto.common.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(InvalidAuthException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidAuth(
            InvalidAuthException ex, HttpServletRequest request
    ) {
        log.error("Authentication error: {}", ex.getMessage());

        return this.buildErrorResponse(
                HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI(), InvalidAuthException.CODE
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidToken(
            InvalidTokenException ex, HttpServletRequest request
    ) {
        log.error("Token validation error: {}", ex.getMessage());

        return this.buildErrorResponse(
                HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI(), ex.getErrorCode()
        );
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleTokenGeneration(
            TokenGenerationException ex, HttpServletRequest request
    ) {
        log.error("Token generation error: {}", ex.getMessage());

        return this.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI(), TokenGenerationException.CODE
        );
    }

}
