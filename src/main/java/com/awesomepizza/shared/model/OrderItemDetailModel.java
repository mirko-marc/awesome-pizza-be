package com.awesomepizza.shared.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class OrderItemDetailModel {
    Long pizzaId;
    String pizzaName;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal lineTotal;
}
