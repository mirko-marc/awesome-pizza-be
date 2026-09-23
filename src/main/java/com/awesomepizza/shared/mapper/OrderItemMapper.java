package com.awesomepizza.shared.mapper;

import com.awesomepizza.shared.entity.OrderItemDB;
import com.awesomepizza.shared.model.OrderItemModel;
import com.awesomepizza.shared.mapper.PizzaMapper;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = PizzaMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderItemMapper {

    @Mapping(target = "orderId", source = "order.id")
    OrderItemModel toModel(OrderItemDB entity);

    @Mapping(target = "order", expression = "java(com.awesomepizza.shared.entity.OrderDB.reference(model.getOrderId()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderItemDB toEntity(OrderItemModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(OrderItemModel model, @MappingTarget OrderItemDB entity);

    @ObjectFactory
    default OrderItemDB newEntity() {
        return OrderItemDB.create();
    }
}



