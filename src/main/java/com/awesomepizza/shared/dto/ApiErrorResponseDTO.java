package com.awesomepizza.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
@Schema(name = "ApiErrorResponse", description = "Uniform API error payload")
public class ApiErrorResponseDTO {
    Instant timestamp;
    int status;
    String error;
    String code;
    String message;
    String path;
    @Builder.Default
    List<FieldValidationErrorDTO> violations = List.of();
}



