package com.awesomepizza.administration.controller;

import com.awesomepizza.administration.dto.AdminOrderDetailDTO;
import com.awesomepizza.administration.dto.AdminOrderSearchFilterDTO;
import com.awesomepizza.administration.dto.AdminOrderSummaryDTO;
import com.awesomepizza.administration.dto.PageResponseDTO;
import com.awesomepizza.administration.service.AdminOrderService;
import com.awesomepizza.shared.enumeration.OrderStatus;
import com.awesomepizza.administration.exception.ActiveOrderInPreparationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
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

    @Test
    void bindsSearchFiltersAndPagination() throws Exception {
        UUID orderCode = UUID.randomUUID();
        PageResponseDTO<AdminOrderSummaryDTO> response = PageResponseDTO.<AdminOrderSummaryDTO>builder()
                .content(List.of(AdminOrderSummaryDTO.builder()
                        .id(42L)
                        .orderCode(orderCode)
                        .status(OrderStatus.RECEIVED)
                        .build()))
                .page(2)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .first(false)
                .last(true)
                .build();
        when(adminOrderService.searchOrders(any(), any())).thenReturn(response);

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

        var filterCaptor = org.mockito.ArgumentCaptor.forClass(AdminOrderSearchFilterDTO.class);
        var pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);
        verify(adminOrderService).searchOrders(filterCaptor.capture(), pageableCaptor.capture());
        assertEquals(42L, filterCaptor.getValue().getId());
        assertEquals(orderCode, filterCaptor.getValue().getOrderCode());
        assertEquals(LocalDate.of(2026, 9, 23), filterCaptor.getValue().getDay());
        assertEquals(OrderStatus.RECEIVED, filterCaptor.getValue().getStatus());
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
        when(adminOrderService.completeOrder(orderCode)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/admin/orders/{orderCode}/complete", orderCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderCode").value(orderCode.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
