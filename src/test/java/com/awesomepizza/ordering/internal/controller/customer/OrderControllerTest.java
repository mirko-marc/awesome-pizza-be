package com.awesomepizza.ordering.internal.controller.customer;

import com.awesomepizza.shared.exception.GlobalExceptionHandler;
import com.awesomepizza.ordering.internal.exception.OrderNotFoundException;
import com.awesomepizza.ordering.internal.mapper.OrderApiMapperImpl;
import com.awesomepizza.ordering.internal.model.CreateOrderModel;
import com.awesomepizza.ordering.internal.model.OrderItemModel;
import com.awesomepizza.ordering.internal.model.OrderModel;
import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import com.awesomepizza.ordering.internal.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({OrderApiMapperImpl.class, GlobalExceptionHandler.class})
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createsAnOrderAsARestResource() throws Exception {
        OrderModel order = order();
        when(orderService.createOrder(any(CreateOrderModel.class))).thenReturn(order);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[{"pizzaId":1,"quantity":2}]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", endsWith("/api/v1/orders/" + order.getOrderCode())))
                .andExpect(jsonPath("$.orderCode").value(order.getOrderCode().toString()))
                .andExpect(jsonPath("$.total").value(17.00));
    }

    @Test
    void returnsAUniformValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.violations[0].field").value("items"));
    }

    @Test
    void returnsNotFoundWhenMonitoringAnUnknownOrder() throws Exception {
        UUID code = UUID.randomUUID();
        when(orderService.getOrder(code)).thenThrow(new OrderNotFoundException(code));

        mockMvc.perform(get("/api/v1/orders/{orderCode}", code))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/v1/orders/" + code));
    }

    @Test
    void returnsTheUniformPayloadForAnInvalidOrderCode() throws Exception {
        mockMvc.perform(get("/api/v1/orders/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REQUEST_ERROR"))
                .andExpect(jsonPath("$.path").value("/api/v1/orders/not-a-uuid"));
    }

    private OrderModel order() {
        OrderItemModel item = OrderItemModel.builder()
                .pizzaId(1L)
                .pizzaName("Margherita")
                .quantity(2)
                .unitPrice(new BigDecimal("8.50"))
                .lineTotal(new BigDecimal("17.00"))
                .build();
        return OrderModel.builder()
                .id(1L)
                .orderCode(UUID.randomUUID())
                .status(OrderStatus.RECEIVED)
                .items(List.of(item))
                .total(new BigDecimal("17.00"))
                .createdAt(Instant.parse("2026-09-23T08:00:00Z"))
                .updatedAt(Instant.parse("2026-09-23T08:00:00Z"))
                .build();
    }
}



