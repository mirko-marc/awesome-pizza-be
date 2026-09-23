package com.awesomepizza.customer.exception;

import com.awesomepizza.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.Collection;

public class PizzaNotAvailableException extends BusinessException {
    public PizzaNotAvailableException(Collection<Long> pizzaIds) {
        super("PIZZA_NOT_AVAILABLE", HttpStatus.UNPROCESSABLE_ENTITY,
                "Le seguenti pizze non esistono o non sono disponibili: " + pizzaIds);
    }
}



