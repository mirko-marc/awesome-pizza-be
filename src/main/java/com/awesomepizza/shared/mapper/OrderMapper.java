package com.awesomepizza.shared.mapper;

import com.awesomepizza.shared.entity.OrderDB;
import com.awesomepizza.shared.entity.OrderItemDB;
import com.awesomepizza.shared.model.OrderDetailModel;
import com.awesomepizza.shared.model.OrderItemDetailModel;
import com.awesomepizza.shared.model.OrderModel;
import com.awesomepizza.shared.model.OrderSummaryModel;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = OrderItemMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderMapper {

    OrderModel toModel(OrderDB entity);

    OrderSummaryModel toSummaryModel(OrderDB entity);

    @Mapping(target = "total", expression = "java(calculateTotal(entity.getItems()))")
    OrderDetailModel toDetailModel(OrderDB entity);

    @Mapping(target = "pizzaId", source = "pizza.id")
    @Mapping(target = "pizzaName", source = "pizza.name")
    @Mapping(target = "lineTotal", expression = "java(calculateLineTotal(entity))")
    OrderItemDetailModel toDetailModel(OrderItemDB entity);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderDB toEntity(OrderModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(OrderModel model, @MappingTarget OrderDB entity);

    @ObjectFactory
    default OrderDB newEntity() {
        return OrderDB.create();
    }

    default BigDecimal calculateTotal(List<OrderItemDB> items) {
        return items.stream()
                .map(this::calculateLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default BigDecimal calculateLineTotal(OrderItemDB item) {
        return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}



