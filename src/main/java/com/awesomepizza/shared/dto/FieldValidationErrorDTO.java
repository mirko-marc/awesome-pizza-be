package com.awesomepizza.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FieldValidationError", description = "Validation error associated with a field")
public record FieldValidationErrorDTO(String field, String message) {
}



