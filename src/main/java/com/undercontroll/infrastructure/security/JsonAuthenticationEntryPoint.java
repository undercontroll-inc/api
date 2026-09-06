package com.undercontroll.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.domain.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@RequiredArgsConstructor
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String FAILURE_CODE_ATTRIBUTE = "auth.failure.code";
    public static final String FAILURE_MESSAGE_ATTRIBUTE = "auth.failure.message";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        String code = (String) request.getAttribute(FAILURE_CODE_ATTRIBUTE);
        String message = (String) request.getAttribute(FAILURE_MESSAGE_ATTRIBUTE);
        if (code == null) {
            code = InvalidTokenException.UNAUTHENTICATED;
            message = "Authentication is required";
        }
        SecurityErrorWriter.write(
                objectMapper,
                request,
                response,
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                message,
                code
        );
    }
}
