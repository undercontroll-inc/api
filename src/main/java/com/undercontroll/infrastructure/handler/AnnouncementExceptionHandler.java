package com.undercontroll.infrastructure.handler;

import com.undercontroll.domain.exception.AnnouncementNotFoundException;
import com.undercontroll.application.dto.common.ExceptionHandlerResponse;
import com.undercontroll.domain.exception.InvalidAnnouncementException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AnnouncementExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(AnnouncementNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleAnnouncementNotFoundException(
            AnnouncementNotFoundException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), AnnouncementNotFoundException.CODE
        );
    }

    @ExceptionHandler(InvalidAnnouncementException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidAnnouncementException(
            InvalidAnnouncementException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), InvalidAnnouncementException.CODE
        );
    }

}
