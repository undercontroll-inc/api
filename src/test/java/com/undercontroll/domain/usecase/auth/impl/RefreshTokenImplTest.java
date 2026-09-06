package com.undercontroll.domain.usecase.auth.impl;

import com.undercontroll.application.dto.auth.RefreshTokenRequest;
import com.undercontroll.application.dto.auth.RefreshTokenResponse;
import com.undercontroll.domain.enums.UserType;
import com.undercontroll.infrastructure.service.RefreshTokenService;
import com.undercontroll.infrastructure.service.RefreshTokenService.RefreshTokenData;
import com.undercontroll.infrastructure.service.TokenServce;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenImplTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private TokenServce tokenServce;

    @InjectMocks
    private RefreshTokenImpl useCase;

    @Test
    @DisplayName("consumes the presented refresh token, then issues a new pair")
    void rotatesRefreshTokenWithoutRevokingOtherSessions() {
        RefreshTokenData current = new RefreshTokenData(
                "old-refresh",
                42,
                "user@example.com",
                UserType.CUSTOMER.name(),
                Instant.now().plusSeconds(3600)
        );

        when(refreshTokenService.consumeRefreshToken("old-refresh")).thenReturn(current);
        when(refreshTokenService.createRefreshToken(42, "user@example.com", UserType.CUSTOMER))
                .thenReturn("new-refresh");
        when(tokenServce.generateToken("42", UserType.CUSTOMER)).thenReturn("new-access");

        RefreshTokenResponse response = useCase.execute(new RefreshTokenRequest("old-refresh"));

        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isEqualTo("new-refresh");
        verify(refreshTokenService).consumeRefreshToken("old-refresh");
        verify(refreshTokenService).createRefreshToken(42, "user@example.com", UserType.CUSTOMER);
        verify(tokenServce).generateToken("42", UserType.CUSTOMER);
        verifyNoMoreInteractions(refreshTokenService);
    }
}
