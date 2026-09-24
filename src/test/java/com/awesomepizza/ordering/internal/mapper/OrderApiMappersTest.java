package com.awesomepizza.ordering.internal.mapper;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import com.awesomepizza.ordering.internal.model.OrderItemModel;
import com.awesomepizza.ordering.internal.model.OrderModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderApiMappersTest {
    private final OrderApiMapper customerMapper = new OrderApiMapperImpl();
    private final AdminOrderMapper administrationMapper = new AdminOrderMapperImpl();

    @Test
    void mapsTheSameApplicationModelToBothHttpContracts() {
        UUID orderCode = UUID.randomUUID();
        OrderModel model = OrderModel.builder()
                .id(7L)
                .orderCode(orderCode)
                .status(OrderStatus.RECEIVED)
                .items(List.of(OrderItemModel.builder()
                        .pizzaId(3L)
                        .pizzaName("Margherita")
                        .quantity(2)
                        .unitPrice(new BigDecimal("8.50"))
                        .lineTotal(new BigDecimal("17.00"))
                        .build()))
                .total(new BigDecimal("17.00"))
                .build();

        var customerResponse = customerMapper.toDto(model);
        var administrationResponse = administrationMapper.toDto(model);

        assertThat(customerResponse.getOrderCode()).isEqualTo(orderCode);
        assertThat(customerResponse.getTotal()).isEqualByComparingTo("17.00");
        assertThat(customerResponse.getItems()).singleElement()
                .extracting("pizzaName", "lineTotal")
                .containsExactly("Margherita", new BigDecimal("17.00"));
        assertThat(administrationResponse.getId()).isEqualTo(7L);
        assertThat(administrationResponse.getOrderCode()).isEqualTo(orderCode);
        assertThat(administrationResponse.getItems()).singleElement()
                .extracting("pizzaName", "lineTotal")
                .containsExactly("Margherita", new BigDecimal("17.00"));
    }
}
