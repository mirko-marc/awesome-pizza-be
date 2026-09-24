package com.awesomepizza.ordering.internal.service;

import com.awesomepizza.ordering.internal.entity.OrderDB;
import com.awesomepizza.ordering.internal.entity.OrderItemDB;
import com.awesomepizza.ordering.internal.entity.PizzaDB;
import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import com.awesomepizza.ordering.internal.event.OrderCreatedEvent;
import com.awesomepizza.ordering.internal.exception.ActiveOrderInPreparationException;
import com.awesomepizza.ordering.internal.exception.InvalidOrderException;
import com.awesomepizza.ordering.internal.exception.InvalidOrderStateException;
import com.awesomepizza.ordering.internal.exception.OrderNotFoundException;
import com.awesomepizza.ordering.internal.exception.PizzaNotAvailableException;
import com.awesomepizza.ordering.internal.mapper.OrderMapper;
import com.awesomepizza.ordering.internal.model.CreateOrderItemModel;
import com.awesomepizza.ordering.internal.model.CreateOrderModel;
import com.awesomepizza.ordering.internal.model.OrderModel;
import com.awesomepizza.ordering.internal.model.OrderSearchCriteria;
import com.awesomepizza.ordering.internal.model.OrderSummaryModel;
import com.awesomepizza.ordering.internal.repository.OrderRepository;
import com.awesomepizza.ordering.internal.repository.PizzaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.awesomepizza.ordering.internal.specification.OrderSpecifications.matching;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private static final int MAX_PAGE_SIZE = 100;

    private final OrderRepository orderRepository;
    private final PizzaRepository pizzaRepository;
    private final OrderMapper orderMapper;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderModel createOrder(CreateOrderModel request) {
        Set<Long> requestedIds = request.getItems().stream()
                .map(CreateOrderItemModel::getPizzaId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (requestedIds.size() != request.getItems().size()) {
            throw new InvalidOrderException("Ogni pizza può comparire una sola volta nell'ordine");
        }

        Map<Long, PizzaDB> availablePizzas = pizzaRepository.findAllById(requestedIds).stream()
                .filter(PizzaDB::isActive)
                .collect(Collectors.toMap(PizzaDB::getId, Function.identity()));
        List<Long> unavailableIds = requestedIds.stream()
                .filter(id -> !availablePizzas.containsKey(id))
                .toList();
        if (!unavailableIds.isEmpty()) {
            throw new PizzaNotAvailableException(unavailableIds);
        }

        OrderDB order = OrderDB.create();
        order.setOrderCode(UUID.randomUUID());
        order.setStatus(OrderStatus.RECEIVED);
        request.getItems().forEach(requestedItem -> {
            PizzaDB pizza = availablePizzas.get(requestedItem.getPizzaId());
            OrderItemDB item = OrderItemDB.create();
            item.setPizza(pizza);
            item.setQuantity(requestedItem.getQuantity());
            item.setUnitPrice(pizza.getPrice());
            order.addItem(item);
        });
        OrderDB savedOrder = orderRepository.saveAndFlush(order);
        OrderModel result = orderMapper.toModel(savedOrder);
        eventPublisher.publishEvent(new OrderCreatedEvent(savedOrder.getOrderCode(), clock.instant()));
        return result;
    }

    @Transactional(readOnly = true)
    public OrderModel getOrder(UUID orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .map(orderMapper::toModel)
                .orElseThrow(() -> new OrderNotFoundException(orderCode));
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryModel> searchOrders(OrderSearchCriteria criteria, Pageable pageable) {
        int pageSize = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        Pageable normalizedPage = PageRequest.of(pageable.getPageNumber(), pageSize,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
        return orderRepository.findAll(matching(criteria, clock.getZone()), normalizedPage)
                .map(orderMapper::toSummaryModel);
    }

    @Transactional
    public OrderModel startPreparation(UUID orderCode) {
        acquirePreparationQueueLock(orderCode);
        OrderDB order = findForUpdate(orderCode);
        requireStatus(order, OrderStatus.RECEIVED);
        if (orderRepository.existsByStatus(OrderStatus.IN_PREPARATION)) {
            throw new ActiveOrderInPreparationException();
        }
        Instant occurredAt = clock.instant();
        order.setStatus(OrderStatus.IN_PREPARATION);
        order.setPreparationStarted(occurredAt);
        OrderModel result = orderMapper.toModel(orderRepository.saveAndFlush(order));
        log.info("Order {} entered preparation", orderCode);
        return result;
    }

    @Transactional
    public OrderModel completeOrder(UUID orderCode) {
        OrderDB order = findForUpdate(orderCode);
        requireStatus(order, OrderStatus.IN_PREPARATION);
        Instant occurredAt = clock.instant();
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(occurredAt);
        OrderModel result = orderMapper.toModel(orderRepository.saveAndFlush(order));
        log.info("Order {} was completed", orderCode);
        return result;
    }

    private void acquirePreparationQueueLock(UUID orderCode) {
        orderRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new OrderNotFoundException(orderCode));
    }

    private OrderDB findForUpdate(UUID orderCode) {
        return orderRepository.findForUpdateByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException(orderCode));
    }

    private void requireStatus(OrderDB order, OrderStatus expectedStatus) {
        if (order.getStatus() != expectedStatus) {
            throw new InvalidOrderStateException(order.getStatus(), expectedStatus);
        }
    }
}
