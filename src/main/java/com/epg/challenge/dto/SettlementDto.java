package com.epg.challenge.dto;

import java.util.Map;
import java.util.stream.Collectors;

public class SettlementDto {
	private String eCommerceId;
	private String month;
	private String paymentProcessorId;
	private Map<Double, Long> transactionsPerAmount;
	
	public SettlementDto(String eCommerceId, String paymentProcessorId, String month, Map<Double, Long> transactionsPerAmount) {
		this.eCommerceId = eCommerceId;
		this.paymentProcessorId = paymentProcessorId;
		this.month = month;
		this.transactionsPerAmount = transactionsPerAmount;
	}

	public String geteCommerceId() {
		return eCommerceId;
	}

	public void seteCommerceId(String eCommerceId) {
		this.eCommerceId = eCommerceId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}
	
	public String getPaymentProcessorId() {
		return paymentProcessorId;
	}

	public void setPaymentProcessorId(String paymentProcessorId) {
		this.paymentProcessorId = paymentProcessorId;
	}
		
	public Map<Double, Long> getTransactionsPerAmount() {
		return transactionsPerAmount;
	}

	public void setTransactionsPerAmount(Map<Double, Long> transactionsPerAmount) {
		this.transactionsPerAmount = transactionsPerAmount;
	}

	@Override
	public String toString() {
		String transactionsPerAmountAsString = transactionsPerAmount.entrySet()
				.stream()
				.map(e -> "Transactions: " + e.getKey() + " - " + "Amount: " + e.getValue() + " Euros.")
				.collect(Collectors.joining("\n"));
		
		return 	"Montly Processing: " + eCommerceId + "\n" +
				"Month: " + month + "\n" +
				"Payment Processor: " + paymentProcessorId + "\n" +
				transactionsPerAmountAsString;
	}

}
