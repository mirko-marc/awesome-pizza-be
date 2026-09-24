package com.awesomepizza.ordering.internal.mapper;

import com.awesomepizza.ordering.internal.entity.OrderItemDB;
import com.awesomepizza.ordering.internal.model.OrderItemModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderItemMapper {

    @Mapping(target = "pizzaId", source = "pizza.id")
    @Mapping(target = "pizzaName", source = "pizza.name")
    @Mapping(target = "lineTotal", expression = "java(calculateLineTotal(entity))")
    OrderItemModel toModel(OrderItemDB entity);

    default java.math.BigDecimal calculateLineTotal(OrderItemDB entity) {
        return entity.getUnitPrice().multiply(java.math.BigDecimal.valueOf(entity.getQuantity()));
    }
}



