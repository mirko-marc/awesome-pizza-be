package com.awesomepizza.customer.service;

import com.awesomepizza.shared.entity.OrderDB;
import com.awesomepizza.shared.entity.OrderItemDB;
import com.awesomepizza.shared.entity.PizzaDB;
import com.awesomepizza.customer.exception.InvalidOrderException;
import com.awesomepizza.shared.exception.OrderNotFoundException;
import com.awesomepizza.customer.exception.PizzaNotAvailableException;
import com.awesomepizza.shared.mapper.OrderMapper;
import com.awesomepizza.customer.model.CreateOrderItemModel;
import com.awesomepizza.customer.model.CreateOrderModel;
import com.awesomepizza.shared.model.OrderModel;
import com.awesomepizza.shared.enumeration.OrderStatus;
import com.awesomepizza.shared.repository.OrderRepository;
import com.awesomepizza.shared.repository.PizzaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final PizzaRepository pizzaRepository;
    private final OrderMapper orderMapper;

    @Override
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

        return orderMapper.toModel(orderRepository.saveAndFlush(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderModel monitorOrder(UUID orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .map(orderMapper::toModel)
                .orElseThrow(() -> new OrderNotFoundException(orderCode));
    }

}



