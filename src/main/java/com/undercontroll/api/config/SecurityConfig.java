package com.undercontroll.api.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public RSAPublicKey publicKey() throws Exception {
        String key = new String(new ClassPathResource("keys/public.pem").getInputStream().readAllBytes());
        key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
    }

    @Bean
    public RSAPrivateKey privateKey() throws Exception {
        String key = new String(new ClassPathResource("keys/private.pem").getInputStream().readAllBytes());
        key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/v1/api/users/auth").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/api/users/auth/google").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/api/users").permitAll()

                        .requestMatchers(HttpMethod.PUT, "/v1/api/users/{userId}").hasAnyAuthority("SCOPE_CUSTOMER", "SCOPE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.GET, "/v1/api/orders/{orderId}").hasAnyAuthority("SCOPE_CUSTOMER", "SCOPE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.GET, "/v1/api/orders/filter").hasAnyAuthority("SCOPE_CUSTOMER", "SCOPE_ADMINISTRATOR")

                        .requestMatchers(HttpMethod.GET, "/v1/api/announcements").hasAnyAuthority("SCOPE_ADMINISTRATOR", "SCOPE_CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/v1/api/announcements").hasAuthority("SCOPE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/v1/api/announcements/**").hasAuthority("SCOPE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/v1/api/announcements/**").hasAuthority("SCOPE_ADMINISTRATOR")

                        .requestMatchers("/v1/api/users/**").hasAuthority("SCOPE_ADMINISTRATOR")
                        .requestMatchers("/v1/api/orders/**").hasAuthority("SCOPE_ADMINISTRATOR")
                        .requestMatchers("/v1/api/components/**").hasAuthority("SCOPE_ADMINISTRATOR")
                        .requestMatchers("/v1/api/service-orders/**").hasAuthority("SCOPE_ADMINISTRATOR")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String userType = jwt.getClaimAsString("userType");
            if (userType != null) {
                return List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("SCOPE_" + userType));
            }
            return List.of();
        });
        return converter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.applyPermitDefaultValues();
        configuration.setAllowedMethods(
                Arrays.asList(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.HEAD.name(),
                        HttpMethod.TRACE.name()
                )
        );

        configuration.setExposedHeaders(List.of(HttpHeaders.CONTENT_TYPE));

        UrlBasedCorsConfigurationSource origin = new UrlBasedCorsConfigurationSource();
        origin.registerCorsConfiguration("/**", configuration);

        return origin;
    }

}
