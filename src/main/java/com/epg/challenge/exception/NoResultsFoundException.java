package com.epg.challenge.exception;

public class NoResultsFoundException extends RuntimeException {

    public NoResultsFoundException() {
    	super(Constants.NO_RESULTS_FOUND);
    }
}
