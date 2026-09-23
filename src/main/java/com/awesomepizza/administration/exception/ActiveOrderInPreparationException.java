package com.awesomepizza.administration.exception;

import com.awesomepizza.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ActiveOrderInPreparationException extends BusinessException {
    public ActiveOrderInPreparationException() {
        super("ACTIVE_ORDER_ALREADY_EXISTS", HttpStatus.CONFLICT,
                "È già presente un ordine in lavorazione");
    }
}
