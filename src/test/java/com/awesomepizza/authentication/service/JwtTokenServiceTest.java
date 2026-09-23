package com.awesomepizza.authentication.service;

import com.awesomepizza.authentication.config.SecurityProperties;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtTokenServiceTest {

    @Test
    void createsAOneHourTokenContainingOnlyApplicationRoles() {
        Instant now = Instant.parse("2026-09-23T10:15:30Z");
        JwtEncoder jwtEncoder = mock(JwtEncoder.class);
        SecurityProperties properties = new SecurityProperties();
        properties.getJwt().setIssuer("awesome-pizza-test");
        properties.getJwt().setAccessTokenTtl(Duration.ofHours(1));
        Jwt encodedJwt = Jwt.withTokenValue("encoded-token")
                .header("alg", "HS256")
                .subject("pizzaiolo")
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofHours(1)))
                .build();
        when(jwtEncoder.encode(org.mockito.ArgumentMatchers.any())).thenReturn(encodedJwt);
        JwtTokenService service = new JwtTokenService(
                jwtEncoder, properties, Clock.fixed(now, ZoneOffset.UTC));
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                "pizzaiolo",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_PIZZA_MAKER"),
                        new SimpleGrantedAuthority("SCOPE_internal")));

        var result = service.createAccessToken(authentication);

        assertThat(result.getToken()).isEqualTo("encoded-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresIn()).isEqualTo(3600);
        ArgumentCaptor<JwtEncoderParameters> parametersCaptor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(parametersCaptor.capture());
        var claims = parametersCaptor.getValue().getClaims();
        assertThat(claims.<String>getClaim("iss")).isEqualTo("awesome-pizza-test");
        assertThat(claims.getSubject()).isEqualTo("pizzaiolo");
        assertThat(claims.getIssuedAt()).isEqualTo(now);
        assertThat(claims.getExpiresAt()).isEqualTo(now.plus(Duration.ofHours(1)));
        assertThat(claims.<List<String>>getClaim("roles"))
                .containsExactly("PIZZA_MAKER");
    }
}
