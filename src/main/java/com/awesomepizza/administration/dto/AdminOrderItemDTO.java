package com.awesomepizza.administration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@Schema(name = "AdminOrderItem", description = "Order line displayed to the pizza maker")
public class AdminOrderItemDTO {
    Long pizzaId;
    String pizzaName;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal lineTotal;
}
