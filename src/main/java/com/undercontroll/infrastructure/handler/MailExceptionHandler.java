package com.undercontroll.infrastructure.handler;

import com.undercontroll.application.dto.common.ExceptionHandlerResponse;
import com.undercontroll.domain.exception.MailSendingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MailExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(MailSendingException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleMailSendingException(
            MailSendingException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to send email: " + ex.getMessage(),
                request.getRequestURI(),
                MailSendingException.CODE,
                ex
        );
    }

}
