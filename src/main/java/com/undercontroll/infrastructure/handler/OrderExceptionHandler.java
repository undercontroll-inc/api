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
public class OrderExceptionHandler extends GenericExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleOrderNotFound(
            OrderNotFoundException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), OrderNotFoundException.CODE
        );
    }

    @ExceptionHandler(UnauthorizedOrderOperation.class)
    public ResponseEntity<ExceptionHandlerResponse> handleUnauthorizedOrderOperation(
            UnauthorizedOrderOperation ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI(), UnauthorizedOrderOperation.CODE
        );
    }

    @ExceptionHandler(InvalidDeleteOrderException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidDeleteOrderException(
            InvalidDeleteOrderException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), InvalidDeleteOrderException.CODE
        );
    }

    @ExceptionHandler(InvalidOrderDateException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidOrderDateException(
            InvalidOrderDateException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), InvalidOrderDateException.CODE
        );
    }

    @ExceptionHandler(InvalidUpdateOrderException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInvalidUpdateOrderException(
            InvalidUpdateOrderException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), InvalidUpdateOrderException.CODE
        );
    }

    @ExceptionHandler(ImmutableOrderException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleImmutableOrderException(
            ImmutableOrderException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), ImmutableOrderException.CODE
        );
    }

    @ExceptionHandler(InsuficientComponentException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleInsuficientComponentException(
            InsuficientComponentException ex, HttpServletRequest request
    ) {
        return this.buildErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI(), InsuficientComponentException.CODE
        );
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<ExceptionHandlerResponse> handlePdfGenerationException(
            PdfGenerationException ex, HttpServletRequest request
    ) {
        log.error("PDF generation error", ex);

        return this.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI(), PdfGenerationException.CODE
        );
    }

    @ExceptionHandler(TemplateLoadException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleTemplateLoadException(
            TemplateLoadException ex, HttpServletRequest request
    ) {
        log.error("Template load error", ex);

        return this.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI(), TemplateLoadException.CODE
        );
    }

    @ExceptionHandler(TempFileException.class)
    public ResponseEntity<ExceptionHandlerResponse> handleTempFileException(
            TempFileException ex, HttpServletRequest request
    ) {
        log.error("Temporary file error", ex);

        return this.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI(), TempFileException.CODE
        );
    }

}
