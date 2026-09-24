package com.awesomepizza.ordering.internal.mapper;

import com.awesomepizza.ordering.internal.entity.OrderDB;
import com.awesomepizza.ordering.internal.model.OrderModel;
import com.awesomepizza.ordering.internal.model.OrderSummaryModel;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = OrderItemMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderMapper {

    @Mapping(target = "total", expression = "java(calculateTotal(entity))")
    OrderModel toModel(OrderDB entity);

    OrderSummaryModel toSummaryModel(OrderDB entity);

    default BigDecimal calculateTotal(OrderDB entity) {
        return entity.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}



