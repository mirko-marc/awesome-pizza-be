package com.awesomepizza.shared.model;

import com.awesomepizza.shared.enumeration.OrderStatus;
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
