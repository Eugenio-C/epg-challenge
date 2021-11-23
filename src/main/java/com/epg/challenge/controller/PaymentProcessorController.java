package com.epg.challenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epg.challenge.dto.BillingDto;
import com.epg.challenge.entity.PaymentProcessor;
import com.epg.challenge.service.PaymentProcessorService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/PaymentProcessors")
public class PaymentProcessorController {
		
	private final PaymentProcessorService paymentProcesorService;
	
	@Autowired
	public PaymentProcessorController(PaymentProcessorService paymentProcesorService) {
		this.paymentProcesorService = paymentProcesorService;
	}
	
	@GetMapping()
	@ApiOperation("Get all the PaymentProcessors available")
	public List<PaymentProcessor> getPaymentProcessors() {
		return paymentProcesorService.getPaymentProcessors();
	}
	
	
	@GetMapping("/{id}")
	@ApiOperation("Get PaymentProcessor info")
	public PaymentProcessor getPaymentProcessor(
			@ApiParam("PaymentProcessor id") @PathVariable("id") String ppId) {

		return paymentProcesorService.getPaymentProcessor(ppId);
	}
	
	
	@PutMapping()
	@ApiOperation("Create or update PaymentProcessor")
	public void putPaymentProcessor(
			@ApiParam("PaymentProcessor to create") @RequestBody PaymentProcessor paymentProcessor) {
			
		paymentProcesorService.savePaymentProcessor(paymentProcessor);		
	}
	
	
	@PutMapping("/{id}/ProcessPaymentFrom/{eCommerceId}/Amount/{amount}")
	@ApiOperation("Process a payment made by ECommerce {eCommerceId} of amount {amount} by PaymentProcessor {id}")
	public void processPayment(
			@ApiParam("PaymentProcessor id") @PathVariable("id") String ppId,
			@ApiParam("ECommerce id") @PathVariable("eCommerceId") String eCommerceId,
			@ApiParam("Amount to pay") @PathVariable("amount") double amount)
	{		
		paymentProcesorService.processPayment(ppId, eCommerceId, amount);		
	}
	
	
	@GetMapping("/{id}/Billing/{year}/{month}")
	@ApiOperation("Obtain billing for PaymentProcessor {id} for date {year}/{month}")
	public BillingDto getBilling(
			@ApiParam("PaymentProcessor id") @PathVariable("id") String ppId,
			@ApiParam("Year (YYYY)") @PathVariable("year") int year,
			@ApiParam("Month (MM)") @PathVariable("month") int month)
	{		
		return paymentProcesorService.getBilling(ppId, year, month);
	}	

}

