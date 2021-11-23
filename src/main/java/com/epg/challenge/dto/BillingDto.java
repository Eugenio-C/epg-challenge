package com.epg.challenge.dto;

import java.util.Map;
import java.util.stream.Collectors;

public class BillingDto {
	private String paymentProcessorId;
	private String month;
	private Map<String, Double> billingPerECommerce;
	
	public BillingDto(String paymentProcessorId, String month, Map<String, Double> billingPerECommerce) {
		this.paymentProcessorId = paymentProcessorId;
		this.month = month;
		this.billingPerECommerce = billingPerECommerce;
	}
	

	public String getPaymentProcessorId() {
		return paymentProcessorId;
	}

	public void setPaymentProcessorId(String paymentProcessorId) {
		this.paymentProcessorId = paymentProcessorId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Map<String, Double> getBillingPerECommerce() {
		return billingPerECommerce;
	}

	public void setBillingPerECommerce(Map<String, Double> billingPerECommerce) {
		this.billingPerECommerce = billingPerECommerce;
	}

	@Override
	public String toString() {
		String billingPerECommerceAsString = billingPerECommerce.entrySet()
				.stream()
				.map(e -> paymentProcessorId + " - " + e.getKey() + ": " + e.getValue() + " EUR")
				.collect(Collectors.joining("\n"));
		
		return   "Payment Processor: " + paymentProcessorId + "\n" +
				 "Month: " + month + "\n" +					 
				 billingPerECommerceAsString;
	}

}
