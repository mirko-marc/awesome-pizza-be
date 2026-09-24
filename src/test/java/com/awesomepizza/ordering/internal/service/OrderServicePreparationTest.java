package com.awesomepizza.ordering.internal.service;

import com.awesomepizza.ordering.internal.exception.ActiveOrderInPreparationException;
import com.awesomepizza.ordering.internal.exception.InvalidOrderStateException;
import com.awesomepizza.ordering.internal.model.OrderModel;
import com.awesomepizza.ordering.internal.model.OrderSearchCriteria;
import com.awesomepizza.ordering.internal.model.OrderSummaryModel;
import com.awesomepizza.ordering.internal.entity.OrderDB;
import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import com.awesomepizza.ordering.internal.mapper.OrderMapper;
import com.awesomepizza.ordering.internal.exception.OrderNotFoundException;
import com.awesomepizza.ordering.internal.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServicePreparationTest {
    private static final Instant NOW = Instant.parse("2026-09-23T10:15:30Z");

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private com.awesomepizza.ordering.internal.repository.PizzaRepository pizzaRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    private OrderService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneId.of("Europe/Rome"));
        service = new OrderService(
                orderRepository, pizzaRepository, orderMapper, clock, eventPublisher);
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchesOrdersWithACappedStablePage() {
        OrderSearchCriteria criteria = OrderSearchCriteria.builder()
                .status(OrderStatus.RECEIVED)
                .build();
        OrderDB entity = order(UUID.randomUUID(), OrderStatus.RECEIVED);
        OrderSummaryModel summary = OrderSummaryModel.builder().id(1L).build();
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(orderMapper.toSummaryModel(entity)).thenReturn(summary);

        var result = service.searchOrders(criteria, PageRequest.of(2, 500));

        assertEquals(List.of(summary), result.getContent());
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderRepository).findAll(any(Specification.class), pageableCaptor.capture());
        assertEquals(2, pageableCaptor.getValue().getPageNumber());
        assertEquals(100, pageableCaptor.getValue().getPageSize());
        assertEquals("createdAt: DESC,id: DESC", pageableCaptor.getValue().getSort().toString());
    }

    @Test
    void startsPreparationWhenNoOtherOrderIsActive() {
        UUID orderCode = UUID.randomUUID();
        OrderDB order = order(orderCode, OrderStatus.RECEIVED);
        OrderModel model = OrderModel.builder().orderCode(orderCode).build();
        when(orderRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(order));
        when(orderRepository.findForUpdateByOrderCode(orderCode)).thenReturn(Optional.of(order));
        when(orderRepository.existsByStatus(OrderStatus.IN_PREPARATION)).thenReturn(false);
        when(orderRepository.saveAndFlush(order)).thenReturn(order);
        when(orderMapper.toModel(order)).thenReturn(model);

        assertSame(model, service.startPreparation(orderCode));
        assertEquals(OrderStatus.IN_PREPARATION, order.getStatus());
        assertEquals(NOW, order.getPreparationStarted());
    }

    @Test
    void returnsTheCompleteOrderDetail() {
        UUID orderCode = UUID.randomUUID();
        OrderDB order = order(orderCode, OrderStatus.RECEIVED);
        OrderModel model = OrderModel.builder().orderCode(orderCode).build();
        when(orderRepository.findByOrderCode(orderCode)).thenReturn(Optional.of(order));
        when(orderMapper.toModel(order)).thenReturn(model);

        assertSame(model, service.getOrder(orderCode));
    }

    @Test
    void returnsNotFoundForAnUnknownOrderDetail() {
        UUID orderCode = UUID.randomUUID();
        when(orderRepository.findByOrderCode(orderCode)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.getOrder(orderCode));
    }

    @Test
    void rejectsStartingAnOrderWhenAnotherOrderIsActive() {
        UUID orderCode = UUID.randomUUID();
        OrderDB order = order(orderCode, OrderStatus.RECEIVED);
        when(orderRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(order));
        when(orderRepository.findForUpdateByOrderCode(orderCode)).thenReturn(Optional.of(order));
        when(orderRepository.existsByStatus(OrderStatus.IN_PREPARATION)).thenReturn(true);

        assertThrows(ActiveOrderInPreparationException.class,
                () -> service.startPreparation(orderCode));
        assertEquals(OrderStatus.RECEIVED, order.getStatus());
    }

    @Test
    void rejectsCompletingAnOrderThatIsNotInPreparation() {
        UUID orderCode = UUID.randomUUID();
        OrderDB order = order(orderCode, OrderStatus.RECEIVED);
        when(orderRepository.findForUpdateByOrderCode(orderCode)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class,
                () -> service.completeOrder(orderCode));
    }

    @Test
    void completesAnOrderInPreparation() {
        UUID orderCode = UUID.randomUUID();
        OrderDB order = order(orderCode, OrderStatus.IN_PREPARATION);
        OrderModel model = OrderModel.builder().orderCode(orderCode).build();
        when(orderRepository.findForUpdateByOrderCode(orderCode)).thenReturn(Optional.of(order));
        when(orderRepository.saveAndFlush(order)).thenReturn(order);
        when(orderMapper.toModel(order)).thenReturn(model);

        assertSame(model, service.completeOrder(orderCode));
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertEquals(NOW, order.getCompletedAt());
    }

    @Test
    void returnsNotFoundWhenThePreparationQueueIsEmpty() {
        UUID orderCode = UUID.randomUUID();
        when(orderRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> service.startPreparation(orderCode));
    }

    private OrderDB order(UUID orderCode, OrderStatus status) {
        OrderDB order = OrderDB.create();
        order.setId(1L);
        order.setOrderCode(orderCode);
        order.setStatus(status);
        return order;
    }
}
