package com.awesomepizza.ordering.internal.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class OrderItemModel {
    Long pizzaId;
    String pizzaName;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal lineTotal;
}




