package com.awesomepizza.ordering.internal.dto.customer;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(name = "CreateOrderRequest", description = "Order creation payload")
public record CreateOrderRequestDTO(
        @NotEmpty
        @Valid
        @ArraySchema(schema = @Schema(implementation = CreateOrderItemRequestDTO.class), minItems = 1)
        List<CreateOrderItemRequestDTO> items) {
}



