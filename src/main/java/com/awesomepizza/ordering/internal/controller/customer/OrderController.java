package com.awesomepizza.ordering.internal.controller.customer;

import com.awesomepizza.shared.dto.ApiErrorResponseDTO;
import com.awesomepizza.ordering.internal.dto.customer.CreateOrderRequestDTO;
import com.awesomepizza.ordering.internal.dto.customer.OrderResponseDTO;
import com.awesomepizza.ordering.internal.mapper.OrderApiMapper;
import com.awesomepizza.ordering.internal.model.CreateOrderModel;
import com.awesomepizza.ordering.internal.model.OrderModel;
import com.awesomepizza.ordering.internal.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order creation and monitoring")
public class OrderController {
    private final OrderService orderService;
    private final OrderApiMapper orderApiMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new order",
            description = "Creates an order using the current prices of available pizzas")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "Pizza unavailable",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody CreateOrderRequestDTO request) {
        CreateOrderModel createOrderModel = orderApiMapper.toModel(request);
        OrderModel createdOrder = orderService.createOrder(createOrderModel);
        OrderResponseDTO response = orderApiMapper.toDto(createdOrder);
        URI resourceLocation = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{orderCode}")
                .buildAndExpand(createdOrder.getOrderCode())
                .toUri();

        return ResponseEntity.created(resourceLocation).body(response);
    }

    @GetMapping(value = "/{orderCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Monitor an order",
            description = "Returns the current order status and timestamps")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    public ResponseEntity<OrderResponseDTO> monitorOrder(
            @Parameter(description = "Public order code", required = true)
            @PathVariable UUID orderCode) {
        OrderModel order = orderService.getOrder(orderCode);
        OrderResponseDTO response = orderApiMapper.toDto(order);

        return ResponseEntity.ok(response);
    }
}



