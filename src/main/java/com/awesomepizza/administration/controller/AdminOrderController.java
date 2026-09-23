package com.awesomepizza.administration.controller;

import com.awesomepizza.administration.dto.AdminOrderDetailDTO;
import com.awesomepizza.administration.dto.AdminOrderSearchFilterDTO;
import com.awesomepizza.administration.dto.AdminOrderSummaryDTO;
import com.awesomepizza.administration.dto.PageResponseDTO;
import com.awesomepizza.administration.mapper.AdminOrderMapper;
import com.awesomepizza.administration.model.OrderSearchCriteria;
import com.awesomepizza.administration.service.AdminOrderService;
import com.awesomepizza.shared.model.OrderDetailModel;
import com.awesomepizza.shared.model.OrderSummaryModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Administration orders", description = "Protected pizza maker order operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {
    private final AdminOrderService adminOrderService;
    private final AdminOrderMapper adminOrderMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search orders", description = "Returns a page of orders. All supplied filters are combined with AND")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders returned"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Pizza maker role required")
    })
    public ResponseEntity<PageResponseDTO<AdminOrderSummaryDTO>> searchOrders(
            @ParameterObject AdminOrderSearchFilterDTO filter,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        OrderSearchCriteria criteria = adminOrderMapper.toCriteria(filter);
        Page<OrderSummaryModel> orders = adminOrderService.searchOrders(criteria, pageable);
        PageResponseDTO<AdminOrderSummaryDTO> response = toPageResponse(orders);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{orderCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get order detail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order returned"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<AdminOrderDetailDTO> getOrder(
            @Parameter(description = "Public order code", required = true)
            @PathVariable UUID orderCode) {
        OrderDetailModel order = adminOrderService.getOrder(orderCode);
        AdminOrderDetailDTO response = adminOrderMapper.toDto(order);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{orderCode}/start", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Start order preparation", description = "Only one order can be in preparation at a time")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preparation started"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Invalid state or another order is already in preparation")
    })
    public ResponseEntity<AdminOrderDetailDTO> startPreparation(
            @Parameter(description = "Public order code", required = true)
            @PathVariable UUID orderCode) {
        OrderDetailModel order = adminOrderService.startPreparation(orderCode);
        AdminOrderDetailDTO response = adminOrderMapper.toDto(order);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{orderCode}/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Complete order preparation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order completed"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order is not in preparation")
    })
    public ResponseEntity<AdminOrderDetailDTO> completeOrder(
            @Parameter(description = "Public order code", required = true)
            @PathVariable UUID orderCode) {
        OrderDetailModel order = adminOrderService.completeOrder(orderCode);
        AdminOrderDetailDTO response = adminOrderMapper.toDto(order);
        return ResponseEntity.ok(response);
    }

    private PageResponseDTO<AdminOrderSummaryDTO> toPageResponse(Page<OrderSummaryModel> orders) {
        return PageResponseDTO.<AdminOrderSummaryDTO>builder()
                .content(orders.getContent().stream().map(adminOrderMapper::toDto).toList())
                .page(orders.getNumber())
                .size(orders.getSize())
                .totalElements(orders.getTotalElements())
                .totalPages(orders.getTotalPages())
                .first(orders.isFirst())
                .last(orders.isLast())
                .build();
    }
}
