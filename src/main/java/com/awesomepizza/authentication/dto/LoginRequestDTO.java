package com.awesomepizza.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Administrative login credentials")
public record LoginRequestDTO(
        @NotBlank
        @Schema(example = "pizzaiolo", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,

        @NotBlank
        @Schema(format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        String password) {
}



