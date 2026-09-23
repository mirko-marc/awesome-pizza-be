package com.awesomepizza.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(name = "CreateOrderItemRequest", description = "Requested pizza and quantity")
public record CreateOrderItemRequestDTO(
        @NotNull
        @Positive
        @Schema(description = "Pizza identifier", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long pizzaId,

        @Positive
        @Schema(description = "Requested quantity", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        int quantity) {
}



