package com.awesomepizza.administration.mapper;

import com.awesomepizza.administration.dto.AdminOrderDetailDTO;
import com.awesomepizza.administration.dto.AdminOrderItemDTO;
import com.awesomepizza.administration.dto.AdminOrderSearchFilterDTO;
import com.awesomepizza.administration.dto.AdminOrderSummaryDTO;
import com.awesomepizza.shared.model.OrderDetailModel;
import com.awesomepizza.shared.model.OrderItemDetailModel;
import com.awesomepizza.administration.model.OrderSearchCriteria;
import com.awesomepizza.shared.model.OrderSummaryModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AdminOrderMapper {
    OrderSearchCriteria toCriteria(AdminOrderSearchFilterDTO filter);

    AdminOrderSummaryDTO toDto(OrderSummaryModel model);

    AdminOrderDetailDTO toDto(OrderDetailModel model);

    AdminOrderItemDTO toDto(OrderItemDetailModel model);
}
