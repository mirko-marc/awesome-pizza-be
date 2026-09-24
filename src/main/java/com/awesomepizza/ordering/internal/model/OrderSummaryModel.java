package com.awesomepizza.ordering.internal.model;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class OrderSummaryModel {
    Long id;
    UUID orderCode;
    OrderStatus status;
    Instant createdAt;
    Instant updatedAt;
    Instant preparationStarted;
    Instant completedAt;
}
