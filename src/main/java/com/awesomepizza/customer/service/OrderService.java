package com.awesomepizza.customer.service;

import com.awesomepizza.customer.model.CreateOrderModel;
import com.awesomepizza.shared.model.OrderModel;

import java.util.UUID;

public interface OrderService {
    OrderModel createOrder(CreateOrderModel request);

    OrderModel monitorOrder(UUID orderCode);
}



