package com.awesomepizza.authentication.service;

import com.awesomepizza.authentication.config.SecurityProperties;
import com.awesomepizza.authentication.model.AccessTokenModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtEncoder jwtEncoder;
    private final SecurityProperties securityProperties;
    private final Clock clock;

    public AccessTokenModel createAccessToken(Authentication authentication) {
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(securityProperties.getJwt().getAccessTokenTtl());
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(securityProperties.getJwt().getIssuer())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(authentication.getName())
                .claim("roles", roles)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return AccessTokenModel.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(securityProperties.getJwt().getAccessTokenTtl().toSeconds())
                .build();
    }
}



