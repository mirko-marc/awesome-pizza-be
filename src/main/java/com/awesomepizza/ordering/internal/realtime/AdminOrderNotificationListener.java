package com.awesomepizza.ordering.internal.realtime;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import com.awesomepizza.ordering.internal.event.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AdminOrderNotificationListener {
    static final String ADMIN_ORDERS_TOPIC = "/topic/admin/orders";

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        send(new AdminOrderNotificationDTO(
                event.orderCode(),
                OrderStatus.RECEIVED,
                "ORDER_CREATED",
                event.occurredAt()));
    }

    private void send(AdminOrderNotificationDTO notification) {
        messagingTemplate.convertAndSend(ADMIN_ORDERS_TOPIC, notification);
    }
}
