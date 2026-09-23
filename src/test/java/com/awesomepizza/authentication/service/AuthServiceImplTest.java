package com.awesomepizza.authentication.service;

import com.awesomepizza.authentication.exception.InvalidCredentialsException;
import com.awesomepizza.authentication.model.AccessTokenModel;
import com.awesomepizza.authentication.model.LoginModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenService jwtTokenService;

    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(authenticationManager, jwtTokenService);
    }

    @Test
    void returnsAnAccessTokenForValidCredentials() {
        LoginModel credentials = LoginModel.builder()
                .username("pizzaiolo")
                .password("correct-password")
                .build();
        Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated(
                "pizzaiolo", null, java.util.List.of());
        AccessTokenModel expected = AccessTokenModel.builder().token("signed-token").build();
        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any()))
                .thenReturn(authenticated);
        when(jwtTokenService.createAccessToken(authenticated)).thenReturn(expected);

        assertThat(service.login(credentials)).isSameAs(expected);

        ArgumentCaptor<Authentication> requestCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getName()).isEqualTo("pizzaiolo");
        assertThat(requestCaptor.getValue().getCredentials()).isEqualTo("correct-password");
    }

    @Test
    void returnsThePublicAuthenticationErrorForInvalidCredentials() {
        LoginModel credentials = LoginModel.builder()
                .username("pizzaiolo")
                .password("wrong-password")
                .build();
        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> service.login(credentials))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
