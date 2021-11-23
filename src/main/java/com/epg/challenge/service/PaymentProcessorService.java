package com.epg.challenge.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epg.challenge.dto.BillingDto;
import com.epg.challenge.entity.AcquirerPlusPricing;
import com.epg.challenge.entity.Payment;
import com.epg.challenge.entity.PaymentProcessor;
import com.epg.challenge.exception.ECommerceNotFoundException;
import com.epg.challenge.exception.NoResultsFoundException;
import com.epg.challenge.exception.PaymentProcessorNotFoundException;
import com.epg.challenge.repository.ECommerceRepository;
import com.epg.challenge.repository.PaymentProcessorRepository;
import com.epg.challenge.repository.PaymentRepository;
import com.epg.challenge.util.Utils;


@Service
public class PaymentProcessorService {
	
	private static final Logger log = LoggerFactory.getLogger(PaymentProcessorService.class);
	//This is a simplification, it should be calculated depending on each month
	private static final int DAYS_PER_MONTH = 30;
	
	private final PaymentProcessorRepository ppRepository;
	private final PaymentRepository paymentRepository;
	private final ECommerceRepository ecRepository;
	
	@Autowired
	public PaymentProcessorService(PaymentProcessorRepository ppRepository, PaymentRepository paymentRepository, ECommerceRepository ecRepository) {
		this.ppRepository = ppRepository;
		this.paymentRepository = paymentRepository;	
		this.ecRepository = ecRepository;
	}
	
	public List<PaymentProcessor> getPaymentProcessors() {
		List<PaymentProcessor> paymentProcessors = (List<PaymentProcessor>) ppRepository.findAll();
		if(paymentProcessors.isEmpty()) throw new NoResultsFoundException();
		return paymentProcessors;		
	}
	
	public PaymentProcessor getPaymentProcessor(String id) {		
		return ppRepository.findById(id).orElseThrow(() -> new PaymentProcessorNotFoundException());
	}
	
	@Transactional
	public void savePaymentProcessor(PaymentProcessor paymentProcessor) {
		ppRepository.save(paymentProcessor);		
	}
	
	@Transactional
	public void processPayment(String paymentProcessorId, String eCommerceId, double amount)
	{
		Utils.checkAmount(amount);
		ppRepository.findById(paymentProcessorId).orElseThrow(() -> new PaymentProcessorNotFoundException());
		ecRepository.findById(eCommerceId).orElseThrow(() -> new ECommerceNotFoundException());
		
		Payment payment = new Payment();
		payment.setEcommerceId(eCommerceId);
		payment.setPaymentProcessorId(paymentProcessorId);
		payment.setAmount(amount);
		payment.setPaymentDate(new java.util.Date());
		
		paymentRepository.save(payment);	
	}
	
		
	public BillingDto getBilling(String ppId, int year, int month) {
		Utils.checkDate(year, month);
		PaymentProcessor pp = ppRepository.findById(ppId).orElseThrow(() -> new PaymentProcessorNotFoundException());
		//Calculate range of days in whole month with convenience methods
		LocalDate startLocalDate = LocalDate.of(year, month, 1);
		LocalDate endLocalDate = startLocalDate.plusMonths(1).minusDays(1);
		//Convert start and end dates to suitable format for repository 
		Date startDate = java.sql.Date.valueOf(startLocalDate);
		Date endDate = java.sql.Date.valueOf(endLocalDate);
		//Generate date string in format "MONTH_NAME YYYY" for billingDto object
		String monthString = startLocalDate.getMonth() + " " + startLocalDate.getYear();
		BillingDto billingDto = null;
		
		//Step 1 - Find all payments processed by PaymentProcessor for the given month
		List<Payment> payments = paymentRepository.findByPaymentProcessorIdAndPaymentDateBetween(ppId, startDate, endDate);
		if(payments.isEmpty()) throw new NoResultsFoundException();							
		log.info("Payments to bill in " + monthString + " for PaymentProcessor " + ppId + ":\n " + payments.toString());

		//Step 2 - Calculate number of transactions by ECommerce and aggregate them
		Map<String, Long> transactionsPerECommerce = payments.stream().collect(Collectors.groupingBy(
					Payment::getEcommerceId, Collectors.counting()));
		
		//Step 3 - Calculate APP for each ECommerce depending on its number of transactions
		Map<String, Double> appPerECommerce = calculateAppPerECommerce(pp, transactionsPerECommerce);
		
		//Step 4 - Group monthly payments by ECommerce and aggregate them
		Map<String, Double> billingPerECommerce = payments.stream().collect(Collectors.groupingBy(
					Payment::getEcommerceId, Collectors.summingDouble(Payment::getAmount)));
		
		//Step 5 - Update aggregated payments by applying flat fee and APP depending on eCommerce
		//in this way: billing = monthlyPaymentTotal * APP + numTransactions * flat fee
		billingPerECommerce.entrySet().forEach(e -> 
			{
				String eCommerceId  = e.getKey();				
				e.setValue(Utils.roundToCents(
								e.getValue() * appPerECommerce.get(eCommerceId) + transactionsPerECommerce.get(eCommerceId) * pp.getFlatFee()
								)
						  );				
			}				
		);		
		
		billingDto = new BillingDto(ppId, monthString, billingPerECommerce);
		log.info("BillingDto returned:\n" + billingDto.toString());

		return billingDto;
	}

	
	private Map<String, Double> calculateAppPerECommerce(PaymentProcessor pp, Map<String, Long> transactionsPerECommerce) {
		Map<String, Double> appPerECommerce = new HashMap<>();
		Comparator<AcquirerPlusPricing> comparator = Comparator.comparing(AcquirerPlusPricing::getVolume);
		pp.getAcquirerPlusPricingList().sort(comparator);
		
		log.info("Transactions per ECommerce: " + transactionsPerECommerce);
		transactionsPerECommerce.entrySet().forEach(e -> 
			{ double app = calculateAppToApply(pp, e.getValue().intValue());
			  appPerECommerce.put(e.getKey(), app);
			}
		);
		
		log.info("APP to apply per ECommerce: " + appPerECommerce);
		return appPerECommerce;
	}
	
	//Assumes PaymentProcessor has a sorted List<AcquirerPlusPricing>
	private double calculateAppToApply(PaymentProcessor pp, int numTransactions) {		
		double appToApply = 0;
		double transactionsPerDay = numTransactions/DAYS_PER_MONTH;
		
		log.info("Calculating APP to apply for " + numTransactions + " transactions and PaymentProcessor " + pp.getId());		
		log.info("APP list is: " + pp.getAcquirerPlusPricingList());

		Iterator<AcquirerPlusPricing> it = pp.getAcquirerPlusPricingList().iterator();
			
		while(it.hasNext()) {
			AcquirerPlusPricing app = it.next();
			appToApply = app.getPercentage();
			if(app.getVolume() > transactionsPerDay) break;
		}
		
		log.info("APP for " +  numTransactions + " transactions and PaymentProcessor " + pp.getId() + " is "+ appToApply);
				
		//return APP expressed as a multiplier instead of a percentage. For example 2% turns into 1,02
		return 1 + (appToApply/100);
	}	

}
