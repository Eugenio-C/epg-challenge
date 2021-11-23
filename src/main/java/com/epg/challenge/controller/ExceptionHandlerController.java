package com.epg.challenge.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.epg.challenge.exception.ECommerceNotFoundException;
import com.epg.challenge.exception.InvalidAmountException;
import com.epg.challenge.exception.InvalidDateException;
import com.epg.challenge.exception.NoResultsFoundException;
import com.epg.challenge.exception.PaymentProcessorNotFoundException;

@ControllerAdvice
public class ExceptionHandlerController {
	
	private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerController.class);	

    @ExceptionHandler({PaymentProcessorNotFoundException.class, ECommerceNotFoundException.class, NoResultsFoundException.class})
    public ResponseEntity<String> handleNotFoundException(RuntimeException e) {
        return error(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({InvalidAmountException.class, InvalidDateException.class})
    public ResponseEntity<String> handleInvalidException(RuntimeException e) {
        return error(HttpStatus.BAD_REQUEST, e);
    }

    private ResponseEntity<String> error(HttpStatus status, Exception e) {
        log.error("Exception : ", e);
        return ResponseEntity.status(status).body(e.getMessage());
    }
}
