package com.awesomepizza.ordering.internal.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder(toBuilder = true)
public class PizzaModel {
    Long id;
    String name;
    String description;
    BigDecimal price;
    boolean active;
    Instant createdAt;
    Instant updatedAt;
}




