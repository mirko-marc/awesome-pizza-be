package com.awesomepizza.shared.mapper;

import com.awesomepizza.shared.entity.OrderDB;
import com.awesomepizza.shared.entity.OrderItemDB;
import com.awesomepizza.shared.entity.PizzaDB;
import com.awesomepizza.shared.enumeration.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {
    private final PizzaMapper pizzaMapper = new PizzaMapperImpl();
    private final OrderItemMapper orderItemMapper = new OrderItemMapperImpl(pizzaMapper);
    private final OrderMapper orderMapper = new OrderMapperImpl(orderItemMapper);

    @Test
    void mapsTheCompleteAdministrativeDetailAndCalculatesTotals() {
        OrderDB order = orderWithOneItem();

        var result = orderMapper.toDetailModel(order);

        assertThat(result.getOrderCode()).isEqualTo(order.getOrderCode());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(result.getTotal()).isEqualByComparingTo("17.00");
        assertThat(result.getItems()).singleElement().satisfies(item -> {
            assertThat(item.getPizzaId()).isEqualTo(3L);
            assertThat(item.getPizzaName()).isEqualTo("Margherita");
            assertThat(item.getQuantity()).isEqualTo(2);
            assertThat(item.getLineTotal()).isEqualByComparingTo("17.00");
        });
    }

    @Test
    void mapsThePublicOrderModelWithNestedPizzaData() {
        OrderDB order = orderWithOneItem();

        var result = orderMapper.toModel(order);

        assertThat(result.getOrderCode()).isEqualTo(order.getOrderCode());
        assertThat(result.getItems()).singleElement().satisfies(item -> {
            assertThat(item.getPizza().getName()).isEqualTo("Margherita");
            assertThat(item.getUnitPrice()).isEqualByComparingTo("8.50");
        });
    }

    private OrderDB orderWithOneItem() {
        PizzaDB pizza = PizzaDB.create();
        pizza.setId(3L);
        pizza.setName("Margherita");
        pizza.setPrice(new BigDecimal("8.50"));
        pizza.setActive(true);
        OrderItemDB item = OrderItemDB.create();
        item.setId(10L);
        item.setPizza(pizza);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("8.50"));
        OrderDB order = OrderDB.create();
        order.setId(7L);
        order.setOrderCode(UUID.randomUUID());
        order.setStatus(OrderStatus.RECEIVED);
        order.addItem(item);
        return order;
    }
}
