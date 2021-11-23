package com.epg.challenge.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.epg.challenge.exception.Constants;
import com.epg.challenge.exception.InvalidAmountException;
import com.epg.challenge.exception.InvalidDateException;


public class Utils {
	public static void checkAmount(double amount) {
		if(amount <= 0) throw new InvalidAmountException();
	}
	
	public static void checkDate(int year, int month) {
		if(month < 1 || month > 12) throw new InvalidDateException(Constants.INVALID_MONTH);
		if(year < 0) throw new InvalidDateException(Constants.INVALID_YEAR);
		if(LocalDate.of(year, month, 1).compareTo(LocalDate.now()) > 0) 
			throw new InvalidDateException(Constants.DATE_MUST_NOT_BE_FUTURE);		
	}
	
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		return new BigDecimal(value).setScale(places, RoundingMode.HALF_EVEN).doubleValue();
	}
	
	public static double roundToCents(double value) {
		return round(value, 2);		
	}

}
