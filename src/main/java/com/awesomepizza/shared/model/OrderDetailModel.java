package com.awesomepizza.shared.model;

import com.awesomepizza.shared.enumeration.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class OrderDetailModel {
    Long id;
    UUID orderCode;
    OrderStatus status;
    List<OrderItemDetailModel> items;
    BigDecimal total;
    Instant createdAt;
    Instant updatedAt;
    Instant preparationStarted;
    Instant completedAt;
}
