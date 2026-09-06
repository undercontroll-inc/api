package com.undercontroll.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.application.dto.common.ExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

final class SecurityErrorWriter {

    private SecurityErrorWriter() {
    }

    static void write(
            ObjectMapper objectMapper,
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String error,
            String message,
            String code
    ) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ExceptionHandlerResponse body = new ExceptionHandlerResponse(
                status,
                error,
                message,
                request.getRequestURI(),
                LocalDateTime.now(ZoneOffset.UTC),
                code
        );
        objectMapper.writeValue(response.getWriter(), body);
    }
}
