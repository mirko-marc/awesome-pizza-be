package com.awesomepizza.ordering.internal.dto.administration;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AdminOrderSearchFilterDTO {
    @Parameter(description = "Internal order identifier", example = "42")
    private Long id;

    @Parameter(description = "Public order code")
    private UUID orderCode;

    @Parameter(description = "Order creation day in the configured business time zone", example = "2026-09-23")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate day;

    @Parameter(description = "Order status")
    private OrderStatus status;
}
