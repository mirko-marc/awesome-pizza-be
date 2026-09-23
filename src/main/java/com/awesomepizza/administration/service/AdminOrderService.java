package com.awesomepizza.administration.service;

import com.awesomepizza.shared.model.OrderDetailModel;
import com.awesomepizza.administration.model.OrderSearchCriteria;
import com.awesomepizza.shared.model.OrderSummaryModel;
import com.awesomepizza.administration.exception.ActiveOrderInPreparationException;
import com.awesomepizza.administration.exception.InvalidOrderStateException;
import com.awesomepizza.shared.entity.OrderDB;
import com.awesomepizza.shared.enumeration.OrderStatus;
import com.awesomepizza.shared.exception.OrderNotFoundException;
import com.awesomepizza.shared.mapper.OrderMapper;
import com.awesomepizza.shared.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

import static com.awesomepizza.administration.specification.OrderSpecifications.matching;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderService {
    private static final int MAX_PAGE_SIZE = 100;

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final Clock clock;

    @Transactional(readOnly = true)
    public Page<OrderSummaryModel> searchOrders(
            OrderSearchCriteria criteria, Pageable pageable) {
        int pageSize = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        Pageable normalizedPage = PageRequest.of(
                pageable.getPageNumber(),
                pageSize,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
        return orderRepository
                .findAll(matching(criteria, clock.getZone()), normalizedPage)
                .map(orderMapper::toSummaryModel);
    }

    @Transactional(readOnly = true)
    public OrderDetailModel getOrder(UUID orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .map(orderMapper::toDetailModel)
                .orElseThrow(() -> new OrderNotFoundException(orderCode));
    }

    @Transactional
    public OrderDetailModel startPreparation(UUID orderCode) {
        acquirePreparationQueueLock(orderCode);
        OrderDB order = findForUpdate(orderCode);
        requireStatus(order, OrderStatus.RECEIVED);

        if (orderRepository.existsByStatus(OrderStatus.IN_PREPARATION)) {
            throw new ActiveOrderInPreparationException();
        }

        order.setStatus(OrderStatus.IN_PREPARATION);
        order.setPreparationStarted(clock.instant());
        OrderDetailModel result = orderMapper.toDetailModel(orderRepository.saveAndFlush(order));
        log.info("Order {} entered preparation", orderCode);
        return result;
    }

    @Transactional
    public OrderDetailModel completeOrder(UUID orderCode) {
        OrderDB order = findForUpdate(orderCode);
        requireStatus(order, OrderStatus.IN_PREPARATION);

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(clock.instant());
        OrderDetailModel result = orderMapper.toDetailModel(orderRepository.saveAndFlush(order));
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
