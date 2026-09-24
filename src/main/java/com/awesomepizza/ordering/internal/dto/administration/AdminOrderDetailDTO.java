package com.awesomepizza.ordering.internal.dto.administration;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import com.awesomepizza.ordering.internal.dto.common.OrderItemResponseDTO;
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
    List<OrderItemResponseDTO> items;
    BigDecimal total;
    Instant createdAt;
    Instant updatedAt;
    Instant preparationStarted;
    Instant completedAt;
}
