package com.awesomepizza.customer.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateOrderItemModel {
    Long pizzaId;
    int quantity;
}



