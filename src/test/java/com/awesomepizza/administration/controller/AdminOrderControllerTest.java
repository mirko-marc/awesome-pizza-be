package com.awesomepizza.administration.controller;

import com.awesomepizza.administration.dto.AdminOrderDetailDTO;
import com.awesomepizza.administration.dto.AdminOrderSearchFilterDTO;
import com.awesomepizza.administration.dto.AdminOrderSummaryDTO;
import com.awesomepizza.administration.dto.PageResponseDTO;
import com.awesomepizza.administration.mapper.AdminOrderMapper;
import com.awesomepizza.administration.model.OrderSearchCriteria;
import com.awesomepizza.administration.service.AdminOrderService;
import com.awesomepizza.shared.enumeration.OrderStatus;
import com.awesomepizza.administration.exception.ActiveOrderInPreparationException;
import com.awesomepizza.shared.model.OrderDetailModel;
import com.awesomepizza.shared.model.OrderSummaryModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminOrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminOrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminOrderService adminOrderService;
    @MockitoBean
    private AdminOrderMapper adminOrderMapper;

    @Test
    void bindsSearchFiltersAndPagination() throws Exception {
        UUID orderCode = UUID.randomUUID();
        OrderSearchCriteria criteria = OrderSearchCriteria.builder()
                .id(42L)
                .orderCode(orderCode)
                .day(LocalDate.of(2026, 9, 23))
                .status(OrderStatus.RECEIVED)
                .build();
        OrderSummaryModel model = OrderSummaryModel.builder()
                .id(42L)
                .orderCode(orderCode)
                .status(OrderStatus.RECEIVED)
                .build();
        AdminOrderSummaryDTO dto = AdminOrderSummaryDTO.builder()
                .id(42L)
                .orderCode(orderCode)
                .status(OrderStatus.RECEIVED)
                .build();
        when(adminOrderMapper.toCriteria(any())).thenReturn(criteria);
        when(adminOrderService.searchOrders(any(), any())).thenReturn(
                new PageImpl<>(List.of(model), PageRequest.of(2, 10), 21));
        when(adminOrderMapper.toDto(model)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/admin/orders")
                        .param("id", "42")
                        .param("orderCode", orderCode.toString())
                        .param("day", "2026-09-23")
                        .param("status", "RECEIVED")
                        .param("page", "2")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(42))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(10));

        var criteriaCaptor = org.mockito.ArgumentCaptor.forClass(OrderSearchCriteria.class);
        var pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);
        verify(adminOrderService).searchOrders(criteriaCaptor.capture(), pageableCaptor.capture());
        assertEquals(42L, criteriaCaptor.getValue().getId());
        assertEquals(orderCode, criteriaCaptor.getValue().getOrderCode());
        assertEquals(LocalDate.of(2026, 9, 23), criteriaCaptor.getValue().getDay());
        assertEquals(OrderStatus.RECEIVED, criteriaCaptor.getValue().getStatus());
        assertEquals(2, pageableCaptor.getValue().getPageNumber());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void returnsConflictWhenAnotherOrderIsInPreparation() throws Exception {
        UUID orderCode = UUID.randomUUID();
        when(adminOrderService.startPreparation(orderCode))
                .thenThrow(new ActiveOrderInPreparationException());

        mockMvc.perform(patch("/api/v1/admin/orders/{orderCode}/start", orderCode))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ACTIVE_ORDER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("È già presente un ordine in lavorazione"));
    }

    @Test
    void returnsOrderDetailAfterCompletion() throws Exception {
        UUID orderCode = UUID.randomUUID();
        AdminOrderDetailDTO response = AdminOrderDetailDTO.builder()
                .id(7L)
                .orderCode(orderCode)
                .status(OrderStatus.COMPLETED)
                .items(List.of())
                .build();
        OrderDetailModel model = OrderDetailModel.builder()
                .id(7L)
                .orderCode(orderCode)
                .status(OrderStatus.COMPLETED)
                .items(List.of())
                .build();
        when(adminOrderService.completeOrder(orderCode)).thenReturn(model);
        when(adminOrderMapper.toDto(model)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/admin/orders/{orderCode}/complete", orderCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderCode").value(orderCode.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
