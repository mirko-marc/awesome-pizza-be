package com.awesomepizza.ordering.internal.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;

@Value
@Builder
public class OrderModel {
    Long id;
    UUID orderCode;
    OrderStatus status;
    Instant preparationStarted;
    Instant completedAt;
    List<OrderItemModel> items;
    BigDecimal total;
    Instant createdAt;
    Instant updatedAt;
}



