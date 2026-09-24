package com.awesomepizza.ordering.internal.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@Schema(name = "PizzaResponse", description = "Pizza available on the menu")
public class PizzaResponseDTO {
    Long id;
    String name;
    String description;
    BigDecimal price;
}



