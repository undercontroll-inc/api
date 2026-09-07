package com.undercontroll.infrastructure.security;

import com.undercontroll.domain.exception.InvalidAuthException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityContextCurrentUserIdTest {

    private final SecurityContextCurrentUserId currentUserId = new SecurityContextCurrentUserId();

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("reads JWT sub (user.getId) the same way AuthContextFilter sets the principal")
    void readsJwtSubjectAsUserId() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("42", null, List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")))
        );

        assertEquals(42, currentUserId.require());
    }

    @Test
    @DisplayName("reads the numeric username when the principal is UserDetails")
    void readsUserDetailsNameAsUserId() {
        var principal = new User("42", "", List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        assertEquals(42, currentUserId.require());
    }

    @Test
    @DisplayName("rejects email in the subject — login uses user id, not email")
    void rejectsEmailSubject() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@shop.com", null, List.of())
        );

        assertThrows(InvalidAuthException.class, currentUserId::require);
    }

    @Test
    @DisplayName("rejects a missing authentication")
    void rejectsMissingAuth() {
        assertThrows(InvalidAuthException.class, currentUserId::require);
    }

    @Test
    @DisplayName("ROLE_ADMINISTRATOR is treated as administrator")
    void administratorRoleIsAdministrator() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("42", null, List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")))
        );

        assertTrue(currentUserId.isAdministrator());
    }

    @Test
    @DisplayName("ROLE_CUSTOMER is not treated as administrator")
    void customerRoleIsNotAdministrator() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("7", null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
        );

        assertFalse(currentUserId.isAdministrator());
    }

    @Test
    @DisplayName("missing authentication is not administrator")
    void missingAuthIsNotAdministrator() {
        assertFalse(currentUserId.isAdministrator());
    }
}
