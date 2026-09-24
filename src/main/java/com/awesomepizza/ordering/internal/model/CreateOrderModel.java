package com.awesomepizza.ordering.internal.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CreateOrderModel {
    List<CreateOrderItemModel> items;
}



