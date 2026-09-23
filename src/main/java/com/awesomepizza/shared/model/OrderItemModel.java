package com.awesomepizza.shared.model;

import com.awesomepizza.shared.model.PizzaModel;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder(toBuilder = true)
public class OrderItemModel {
    Long id;
    Long orderId;
    PizzaModel pizza;
    int quantity;
    BigDecimal unitPrice;
    Instant createdAt;
    Instant updatedAt;
}




