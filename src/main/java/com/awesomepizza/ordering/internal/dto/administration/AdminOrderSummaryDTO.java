package com.awesomepizza.ordering.internal.dto.administration;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
@Schema(name = "AdminOrderSummary", description = "Order data displayed in the administration queue")
public class AdminOrderSummaryDTO {
    Long id;
    UUID orderCode;
    OrderStatus status;
    Instant createdAt;
    Instant updatedAt;
    Instant preparationStarted;
    Instant completedAt;
}
