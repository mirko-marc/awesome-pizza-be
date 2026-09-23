package com.awesomepizza.customer.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CreateOrderModel {
    List<CreateOrderItemModel> items;
}



