package com.epg.challenge.exception;


public class PaymentProcessorNotFoundException extends RuntimeException {

    public PaymentProcessorNotFoundException() {
    	super(Constants.PAYMENT_PROCESSOR_NOT_FOUND);
    }

}
