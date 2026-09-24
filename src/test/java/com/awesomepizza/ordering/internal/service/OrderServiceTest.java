package com.awesomepizza.ordering.internal.service;

import com.awesomepizza.ordering.internal.entity.OrderDB;
import com.awesomepizza.ordering.internal.entity.PizzaDB;
import com.awesomepizza.ordering.internal.exception.InvalidOrderException;
import com.awesomepizza.ordering.internal.exception.OrderNotFoundException;
import com.awesomepizza.ordering.internal.exception.PizzaNotAvailableException;
import com.awesomepizza.ordering.internal.mapper.OrderMapper;
import com.awesomepizza.ordering.internal.model.CreateOrderItemModel;
import com.awesomepizza.ordering.internal.model.CreateOrderModel;
import com.awesomepizza.ordering.internal.model.OrderModel;
import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import com.awesomepizza.ordering.internal.event.OrderCreatedEvent;
import com.awesomepizza.ordering.internal.repository.OrderRepository;
import com.awesomepizza.ordering.internal.repository.PizzaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService(orderRepository, pizzaRepository, orderMapper,
                java.time.Clock.systemUTC(), eventPublisher);
    }

    @Test
    void createsAnOrderUsingTheCurrentPizzaPrice() {
        PizzaDB pizza = pizza(1L, "Margherita", "8.50");
        CreateOrderModel request = CreateOrderModel.builder().items(List.of(
                CreateOrderItemModel.builder().pizzaId(1L).quantity(2).build())).build();
        OrderModel expected = OrderModel.builder().orderCode(UUID.randomUUID()).build();

        when(pizzaRepository.findAllById(Set.of(1L))).thenReturn(List.of(pizza));
        when(orderRepository.saveAndFlush(any(OrderDB.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toModel(any(OrderDB.class))).thenReturn(expected);

        assertSame(expected, service.createOrder(request));

        ArgumentCaptor<OrderDB> orderCaptor = ArgumentCaptor.forClass(OrderDB.class);
        verify(orderRepository).saveAndFlush(orderCaptor.capture());
        OrderDB savedOrder = orderCaptor.getValue();
        assertNotNull(savedOrder.getOrderCode());
        assertEquals(OrderStatus.RECEIVED, savedOrder.getStatus());
        assertEquals(1, savedOrder.getItems().size());
        assertEquals(2, savedOrder.getItems().getFirst().getQuantity());
        assertEquals(new BigDecimal("8.50"), savedOrder.getItems().getFirst().getUnitPrice());
        assertSame(savedOrder, savedOrder.getItems().getFirst().getOrder());
        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(savedOrder.getOrderCode(), eventCaptor.getValue().orderCode());
    }

    @Test
    void rejectsDuplicatePizzas() {
        CreateOrderModel request = CreateOrderModel.builder().items(List.of(
                CreateOrderItemModel.builder().pizzaId(1L).quantity(1).build(),
                CreateOrderItemModel.builder().pizzaId(1L).quantity(2).build())).build();

        assertThrows(InvalidOrderException.class, () -> service.createOrder(request));
    }

    @Test
    void rejectsUnavailablePizzas() {
        CreateOrderModel request = CreateOrderModel.builder().items(List.of(
                CreateOrderItemModel.builder().pizzaId(99L).quantity(1).build())).build();
        when(pizzaRepository.findAllById(Set.of(99L))).thenReturn(List.of());

        assertThrows(PizzaNotAvailableException.class, () -> service.createOrder(request));
    }

    @Test
    void rejectsAnInactivePizzaEvenWhenItExists() {
        CreateOrderModel request = CreateOrderModel.builder().items(List.of(
                CreateOrderItemModel.builder().pizzaId(5L).quantity(1).build())).build();
        PizzaDB inactivePizza = pizza(5L, "Seasonal pizza", "10.00");
        inactivePizza.setActive(false);
        when(pizzaRepository.findAllById(Set.of(5L))).thenReturn(List.of(inactivePizza));

        assertThrows(PizzaNotAvailableException.class, () -> service.createOrder(request));
    }

    @Test
    void returnsTheOrderForMonitoring() {
        UUID orderCode = UUID.randomUUID();
        OrderDB entity = OrderDB.create();
        entity.setOrderCode(orderCode);
        OrderModel expected = OrderModel.builder().orderCode(orderCode).build();
        when(orderRepository.findByOrderCode(orderCode)).thenReturn(Optional.of(entity));
        when(orderMapper.toModel(entity)).thenReturn(expected);

        assertSame(expected, service.getOrder(orderCode));
    }

    @Test
    void returnsNotFoundForAnUnknownOrder() {
        UUID orderCode = UUID.randomUUID();
        when(orderRepository.findByOrderCode(orderCode)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.getOrder(orderCode));
    }

    private PizzaDB pizza(Long id, String name, String price) {
        PizzaDB pizza = PizzaDB.create();
        pizza.setId(id);
        pizza.setName(name);
        pizza.setPrice(new BigDecimal(price));
        pizza.setActive(true);
        return pizza;
    }

}



