package com.undercontroll.api.adapters;

import com.undercontroll.api.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Component
public class JwtTokenService implements TokenService {

    private final JwtEncoder jwtEncoder;

    @Override
    public String generateToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .issuer("undercontroll-inc")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS)) // 1 hour
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}