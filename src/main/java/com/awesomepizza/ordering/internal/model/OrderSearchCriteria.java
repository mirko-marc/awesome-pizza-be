package com.awesomepizza.ordering.internal.model;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class OrderSearchCriteria {
    Long id;
    UUID orderCode;
    LocalDate day;
    OrderStatus status;
}
