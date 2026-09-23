package com.awesomepizza.customer.dto;

import com.awesomepizza.shared.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "OrderResponse", description = "Order details")
public class OrderResponseDTO {
    private UUID orderCode;
    private OrderStatus status;
    private List<OrderItemResponseDTO> items;
    private BigDecimal total;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant preparationStarted;
    private Instant completedAt;
}



