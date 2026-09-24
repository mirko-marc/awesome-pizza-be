package com.awesomepizza.ordering.internal.mapper;

import com.awesomepizza.ordering.internal.dto.administration.AdminOrderDetailDTO;
import com.awesomepizza.ordering.internal.dto.common.OrderItemResponseDTO;
import com.awesomepizza.ordering.internal.dto.administration.AdminOrderSearchFilterDTO;
import com.awesomepizza.ordering.internal.dto.administration.AdminOrderSummaryDTO;
import com.awesomepizza.ordering.internal.model.OrderModel;
import com.awesomepizza.ordering.internal.model.OrderItemModel;
import com.awesomepizza.ordering.internal.model.OrderSearchCriteria;
import com.awesomepizza.ordering.internal.model.OrderSummaryModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AdminOrderMapper {
    OrderSearchCriteria toCriteria(AdminOrderSearchFilterDTO filter);

    AdminOrderSummaryDTO toDto(OrderSummaryModel model);

    AdminOrderDetailDTO toDto(OrderModel model);

    OrderItemResponseDTO toDto(OrderItemModel model);
}
