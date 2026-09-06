package com.undercontroll.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undercontroll.application.controller.impl.AuthController;
import com.undercontroll.application.dto.auth.AuthUserRequest;
import com.undercontroll.application.dto.auth.AuthUserResponse;
import com.undercontroll.application.dto.auth.RefreshTokenRequest;
import com.undercontroll.application.dto.auth.RefreshTokenResponse;
import com.undercontroll.application.dto.user.UserDto;
import com.undercontroll.domain.enums.AuthProvider;
import com.undercontroll.domain.enums.UserType;
import com.undercontroll.domain.usecase.auth.AuthUserPort;
import com.undercontroll.domain.usecase.auth.RefreshTokenPort;
import com.undercontroll.infrastructure.config.RateLimitProperties;
import com.undercontroll.infrastructure.config.SecurityConfig;
import com.undercontroll.infrastructure.service.TokenServce;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, RateLimitProperties.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthUserPort authUserPort;

    @MockitoBean
    private RefreshTokenPort refreshTokenPort;

    @MockitoBean
    private TokenServce tokenServce;

    @Test
    @DisplayName("POST /v1/api/auth - Should authenticate with password and return 200 with token")
    void shouldAuthenticateUserSuccessfully() throws Exception {
        AuthUserRequest request = new AuthUserRequest(
                AuthProvider.PASSWORD, "john@example.com", "password123", null);

        UserDto userDto = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        when(authUserPort.execute(any(AuthUserRequest.class)))
                .thenReturn(new AuthUserResponse("jwt-token-here", "refresh-token-here", userDto));

        mockMvc.perform(post("/v1/api/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-here"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));

        verify(authUserPort, times(1)).execute(any(AuthUserRequest.class));
    }

    @Test
    @DisplayName("POST /v1/api/auth - Should authenticate with Google and return 200")
    void shouldAuthenticateWithGoogleSuccessfully() throws Exception {
        AuthUserRequest request = new AuthUserRequest(
                AuthProvider.GOOGLE, "john@example.com", null, "google-token");

        UserDto userDto = new UserDto(1, "John", "john@example.com", "Doe",
                "Street 123", "12345678900", "12345-678", "11999999999",
                null, false, false, true, UserType.CUSTOMER);

        when(authUserPort.execute(any(AuthUserRequest.class)))
                .thenReturn(new AuthUserResponse("jwt-token-here", "refresh-token-here", userDto));

        mockMvc.perform(post("/v1/api/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-here"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));

        verify(authUserPort, times(1)).execute(any(AuthUserRequest.class));
    }

    @Test
    @DisplayName("POST /v1/api/auth/refresh - Should exchange refresh token and return 200")
    void shouldRefreshTokenSuccessfully() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("old-refresh-token");

        when(refreshTokenPort.execute(any(RefreshTokenRequest.class)))
                .thenReturn(new RefreshTokenResponse("new-access-token", "new-refresh-token"));

        mockMvc.perform(post("/v1/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));

        verify(refreshTokenPort, times(1)).execute(any(RefreshTokenRequest.class));
    }
}
