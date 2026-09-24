package com.awesomepizza.ordering.internal.realtime;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record AdminOrderNotificationDTO(
        UUID orderCode,
        OrderStatus status,
        String eventType,
        Instant occurredAt) {
}
