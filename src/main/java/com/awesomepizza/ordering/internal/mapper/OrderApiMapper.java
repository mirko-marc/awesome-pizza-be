package com.awesomepizza.ordering.internal.mapper;

import com.awesomepizza.ordering.internal.dto.customer.CreateOrderItemRequestDTO;
import com.awesomepizza.ordering.internal.dto.customer.CreateOrderRequestDTO;
import com.awesomepizza.ordering.internal.dto.common.OrderItemResponseDTO;
import com.awesomepizza.ordering.internal.dto.customer.OrderResponseDTO;
import com.awesomepizza.ordering.internal.model.CreateOrderItemModel;
import com.awesomepizza.ordering.internal.model.CreateOrderModel;
import com.awesomepizza.ordering.internal.model.OrderItemModel;
import com.awesomepizza.ordering.internal.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderApiMapper {

    CreateOrderModel toModel(CreateOrderRequestDTO request);

    CreateOrderItemModel toModel(CreateOrderItemRequestDTO request);

    OrderResponseDTO toDto(OrderModel model);

    OrderItemResponseDTO toDto(OrderItemModel model);
}



