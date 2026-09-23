package com.awesomepizza.customer.exception;

import com.awesomepizza.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidOrderException extends BusinessException {
    public InvalidOrderException(String message) {
        super("INVALID_ORDER", HttpStatus.BAD_REQUEST, message);
    }
}



