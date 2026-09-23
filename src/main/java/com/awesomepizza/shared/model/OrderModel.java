package com.awesomepizza.shared.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.awesomepizza.shared.enumeration.OrderStatus;

@Value
@Builder(toBuilder = true)
public class OrderModel {
    Long id;
    UUID orderCode;
    OrderStatus status;
    Instant preparationStarted;
    Instant completedAt;
    List<OrderItemModel> items;
    Instant createdAt;
    Instant updatedAt;
}



