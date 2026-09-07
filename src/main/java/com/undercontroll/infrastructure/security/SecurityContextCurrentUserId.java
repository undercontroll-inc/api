package com.undercontroll.infrastructure.security;

import com.undercontroll.domain.exception.InvalidAuthException;
import com.undercontroll.domain.gateway.CurrentUserAdminPort;
import com.undercontroll.domain.gateway.CurrentUserIdPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextCurrentUserId implements CurrentUserIdPort, CurrentUserAdminPort {

    @Override
    public Integer require() {
        Authentication authentication = authentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidAuthException("User is not authenticated");
        }
        String subject = authentication.getName();
        if (subject == null || subject.isBlank()) {
            throw new InvalidAuthException("User is not authenticated");
        }
        try {
            return Integer.valueOf(subject);
        } catch (NumberFormatException ex) {
            throw new InvalidAuthException("User is not authenticated");
        }
    }

    @Override
    public boolean isAdministrator() {
        Authentication authentication = authentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMINISTRATOR"::equals);
    }

    private static Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
