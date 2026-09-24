package com.awesomepizza.ordering.internal.realtime;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import com.awesomepizza.ordering.internal.event.OrderCreatedEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminOrderNotificationListenerTest {
    private static final Instant NOW = Instant.parse("2026-09-24T10:30:00Z");

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void sendsCreatedOrdersToTheAdminTopic() {
        UUID orderCode = UUID.randomUUID();
        AdminOrderNotificationListener listener = new AdminOrderNotificationListener(messagingTemplate);

        listener.onOrderCreated(new OrderCreatedEvent(orderCode, NOW));

        AdminOrderNotificationDTO notification = capturedNotification();
        assertThat(notification.orderCode()).isEqualTo(orderCode);
        assertThat(notification.status()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(notification.eventType()).isEqualTo("ORDER_CREATED");
        assertThat(notification.occurredAt()).isEqualTo(NOW);
    }

    private AdminOrderNotificationDTO capturedNotification() {
        ArgumentCaptor<AdminOrderNotificationDTO> captor =
                ArgumentCaptor.forClass(AdminOrderNotificationDTO.class);
        verify(messagingTemplate).convertAndSend(
                org.mockito.ArgumentMatchers.eq(AdminOrderNotificationListener.ADMIN_ORDERS_TOPIC),
                captor.capture());
        return captor.getValue();
    }
}
