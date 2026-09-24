package com.awesomepizza.authentication.security;

import com.awesomepizza.authentication.config.SecurityConfig;
import com.awesomepizza.ordering.internal.controller.administration.AdminController;
import com.awesomepizza.authentication.controller.AuthController;
import com.awesomepizza.authentication.mapper.AuthApiMapperImpl;
import com.awesomepizza.authentication.model.AccessTokenModel;
import com.awesomepizza.authentication.service.AuthService;
import com.awesomepizza.authentication.service.JwtTokenService;
import com.awesomepizza.shared.config.TimeConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, AdminController.class})
@Import({SecurityConfig.class, SecurityErrorHandler.class,
        AuthApiMapperImpl.class, JwtTokenService.class, TimeConfig.class})
class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @Test
    void exposesLoginWithoutAuthentication() throws Exception {
        AccessTokenModel token = AccessTokenModel.builder()
                .token("signed.jwt.token")
                .tokenType("Bearer")
                .expiresIn(300)
                .build();
        when(authService.login(any())).thenReturn(token);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"pizzaiolo","password":"secret"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("signed.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void rejectsAnonymousAdminRequests() throws Exception {
        mockMvc.perform(get("/api/v1/admin"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void rejectsAuthenticatedUsersWithoutPizzaMakerRole() throws Exception {
        String accessToken = accessToken("pizzaiolo", "ROLE_CUSTOMER");

        mockMvc.perform(get("/api/v1/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    void allowsPizzaMakerToAccessAdminApi() throws Exception {
        String accessToken = accessToken("pizzaiolo", "ROLE_PIZZA_MAKER");

        mockMvc.perform(get("/api/v1/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("pizzaiolo"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_PIZZA_MAKER"));
    }

    private String accessToken(String username, String authority) {
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                username, null, List.of(new SimpleGrantedAuthority(authority)));
        return jwtTokenService.createAccessToken(authentication).getToken();
    }
}



