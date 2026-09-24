package com.awesomepizza.ordering.internal.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@Schema(name = "OrderItemResponse", description = "Order line details")
public class OrderItemResponseDTO {
    Long pizzaId;
    String pizzaName;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal lineTotal;
}



