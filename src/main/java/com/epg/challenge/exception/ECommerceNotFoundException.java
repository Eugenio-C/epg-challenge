package com.epg.challenge.exception;

public class ECommerceNotFoundException extends RuntimeException {

    public ECommerceNotFoundException() {
    	super(Constants.ECOMMERCE_NOT_FOUND);
    }
}
