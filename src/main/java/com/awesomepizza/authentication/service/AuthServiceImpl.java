package com.awesomepizza.authentication.service;

import com.awesomepizza.authentication.exception.InvalidCredentialsException;
import com.awesomepizza.authentication.model.AccessTokenModel;
import com.awesomepizza.authentication.model.LoginModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    @Override
    public AccessTokenModel login(LoginModel credentials) {
        UsernamePasswordAuthenticationToken request = UsernamePasswordAuthenticationToken.unauthenticated(
                credentials.getUsername(), credentials.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(request);
            return jwtTokenService.createAccessToken(authentication);
        } catch (AuthenticationException exception) {
            throw new InvalidCredentialsException();
        }
    }
}



