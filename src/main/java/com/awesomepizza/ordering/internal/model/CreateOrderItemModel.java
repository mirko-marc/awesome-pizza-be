package com.awesomepizza.ordering.internal.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateOrderItemModel {
    Long pizzaId;
    int quantity;
}



