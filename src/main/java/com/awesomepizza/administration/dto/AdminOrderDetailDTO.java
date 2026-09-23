package com.awesomepizza.administration.dto;

import com.awesomepizza.shared.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder
@Schema(name = "AdminOrderDetail", description = "Complete order data for administration")
public class AdminOrderDetailDTO {
    Long id;
    UUID orderCode;
    OrderStatus status;
    List<AdminOrderItemDTO> items;
    BigDecimal total;
    Instant createdAt;
    Instant updatedAt;
    Instant preparationStarted;
    Instant completedAt;
}
