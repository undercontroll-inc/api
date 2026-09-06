package com.undercontroll.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.domain.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        SecurityErrorWriter.write(
                objectMapper,
                request,
                response,
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "The user does not have permission to access this resource",
                InvalidTokenException.ACCESS_DENIED
        );
    }
}
