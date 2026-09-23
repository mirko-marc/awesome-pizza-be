package com.awesomepizza.authentication.controller;

import com.awesomepizza.shared.dto.ApiErrorResponseDTO;
import com.awesomepizza.authentication.dto.LoginRequestDTO;
import com.awesomepizza.authentication.dto.LoginResponseDTO;
import com.awesomepizza.authentication.mapper.AuthApiMapper;
import com.awesomepizza.authentication.model.AccessTokenModel;
import com.awesomepizza.authentication.model.LoginModel;
import com.awesomepizza.authentication.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Administrative authentication")
public class AuthController {
    private final AuthService authService;
    private final AuthApiMapper authApiMapper;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Authenticate an administrative user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginModel credentials = authApiMapper.toModel(request);
        AccessTokenModel token = authService.login(credentials);
        LoginResponseDTO response = authApiMapper.toDTO(token);

        return ResponseEntity.ok(response);
    }
}



