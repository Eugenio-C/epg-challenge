package com.epg.challenge.exception;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException() {
    	super(Constants.INVALID_AMOUNT);
    }
}
