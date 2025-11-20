package com.undercontroll.api.handlers;

import com.undercontroll.api.dto.ExceptionHandlerResponse;
import com.undercontroll.api.exception.MailSendingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MailExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(MailSendingException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleMailSendingException(
            MailSendingException ex, HttpServletRequest request
    ) {
        log.error("Error sending email: {}", ex.getMessage());

        return this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email: " + ex.getMessage(), request.getRequestURI());
    }

}

