package com.awesomepizza.ordering.internal.exception;

import com.awesomepizza.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class OrderNotFoundException extends BusinessException {
    public OrderNotFoundException(UUID orderCode) {
        super("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND,
                "Nessun ordine trovato con codice " + orderCode);
    }
}



