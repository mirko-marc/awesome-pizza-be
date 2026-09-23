package com.awesomepizza.authentication.security;

import com.awesomepizza.shared.dto.ApiErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        writeError(response, request, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
                "Autenticazione richiesta o token non valido");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        writeError(response, request, HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "Non disponi dei permessi necessari");
    }

    private void writeError(HttpServletResponse response, HttpServletRequest request,
                            HttpStatus status, String code, String message) throws IOException {
        ApiErrorResponseDTO body = ApiErrorResponseDTO.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .path(request.getRequestURI())
                .violations(List.of())
                .build();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}



