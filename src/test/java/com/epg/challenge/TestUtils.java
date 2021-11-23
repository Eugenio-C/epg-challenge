package com.epg.challenge;

import java.time.LocalDate;

import com.epg.challenge.entity.Payment;

public class TestUtils {
	public static Payment createPayment(int id, String ppId, String ecId, LocalDate date, double amount) {
		Payment payment = new Payment();
		payment.setId(id);
		payment.setPaymentProcessorId(ppId);
		payment.setEcommerceId(ecId);
		payment.setPaymentDate(java.sql.Date.valueOf(date));
		payment.setAmount(amount);
		
		return payment;
	}
}
