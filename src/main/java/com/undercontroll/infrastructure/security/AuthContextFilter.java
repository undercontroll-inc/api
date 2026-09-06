package com.undercontroll.infrastructure.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.undercontroll.domain.exception.InvalidTokenException;
import com.undercontroll.infrastructure.service.TokenServce;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthContextFilter extends OncePerRequestFilter {

    private final TokenServce tokenServce;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            DecodedJWT decoded = tokenServce.validateToken(token);

            String userId = decoded.getSubject();
            String role = decoded.getClaim("roles").asString();

            if (role == null || role.isBlank()) {
                log.warn("No roles claim found in token for user: {}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            log.info("User id: {} resolved with roles: {}", userId, role);

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, List.of(authority));

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (InvalidTokenException e) {
            request.setAttribute(JsonAuthenticationEntryPoint.FAILURE_CODE_ATTRIBUTE, e.getCode());
            request.setAttribute(JsonAuthenticationEntryPoint.FAILURE_MESSAGE_ATTRIBUTE, e.getMessage());
            log.debug("JWT rejected for {}: {}", request.getRequestURI(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
