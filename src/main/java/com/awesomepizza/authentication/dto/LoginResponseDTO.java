package com.awesomepizza.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoginResponse", description = "JWT access token")
public class LoginResponseDTO {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
}



