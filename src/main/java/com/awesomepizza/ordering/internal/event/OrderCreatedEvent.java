package com.awesomepizza.ordering.internal.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(UUID orderCode, Instant occurredAt) {
}
