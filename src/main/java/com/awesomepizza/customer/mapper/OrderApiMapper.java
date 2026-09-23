package com.awesomepizza.customer.mapper;

import com.awesomepizza.customer.dto.CreateOrderItemRequestDTO;
import com.awesomepizza.customer.dto.CreateOrderRequestDTO;
import com.awesomepizza.customer.dto.OrderItemResponseDTO;
import com.awesomepizza.customer.dto.OrderResponseDTO;
import com.awesomepizza.customer.model.CreateOrderItemModel;
import com.awesomepizza.customer.model.CreateOrderModel;
import com.awesomepizza.shared.model.OrderItemModel;
import com.awesomepizza.shared.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderApiMapper {

    CreateOrderModel toModel(CreateOrderRequestDTO request);

    CreateOrderItemModel toModel(CreateOrderItemRequestDTO request);

    @Mapping(target = "total", expression = "java(calculateTotal(model.getItems()))")
    OrderResponseDTO toDto(OrderModel model);

    @Mapping(target = "pizzaId", source = "pizza.id")
    @Mapping(target = "pizzaName", source = "pizza.name")
    @Mapping(target = "lineTotal", expression = "java(calculateLineTotal(model))")
    OrderItemResponseDTO toDto(OrderItemModel model);

    default BigDecimal calculateTotal(List<OrderItemModel> items) {
        if (items == null) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(this::calculateLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default BigDecimal calculateLineTotal(OrderItemModel item) {
        return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}



