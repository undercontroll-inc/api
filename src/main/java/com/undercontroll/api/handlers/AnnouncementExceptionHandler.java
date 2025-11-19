package com.undercontroll.api.handlers;

import com.undercontroll.api.dto.ExceptionHandlerResponse;
import com.undercontroll.api.exception.AnnouncementNotFoundException;
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

        return this.buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

}

