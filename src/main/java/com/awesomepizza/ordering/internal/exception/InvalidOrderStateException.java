package com.awesomepizza.ordering.internal.exception;

import com.awesomepizza.ordering.internal.enumeration.OrderStatus;
import com.awesomepizza.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidOrderStateException extends BusinessException {
    public InvalidOrderStateException(OrderStatus currentStatus, OrderStatus expectedStatus) {
        super("INVALID_ORDER_STATE", HttpStatus.CONFLICT,
                "L'ordine è nello stato " + currentStatus
                        + " ma l'operazione richiede lo stato " + expectedStatus);
    }
}
