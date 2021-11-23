package com.epg.challenge.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epg.challenge.dto.SettlementDto;
import com.epg.challenge.entity.ECommerce;
import com.epg.challenge.entity.Payment;
import com.epg.challenge.exception.ECommerceNotFoundException;
import com.epg.challenge.exception.NoResultsFoundException;
import com.epg.challenge.exception.PaymentProcessorNotFoundException;
import com.epg.challenge.repository.ECommerceRepository;
import com.epg.challenge.repository.PaymentProcessorRepository;
import com.epg.challenge.repository.PaymentRepository;
import com.epg.challenge.util.Utils;

@Service
public class ECommerceService {

	private static final Logger log = LoggerFactory.getLogger(ECommerceService.class);
	
	private final PaymentProcessorRepository ppRepository;
	private final PaymentRepository paymentRepository;
	private final ECommerceRepository ecRepository;
	
	@Autowired
	public ECommerceService(PaymentProcessorRepository ppRepository, PaymentRepository paymentRepository, ECommerceRepository ecRepository) {
		this.ppRepository = ppRepository;
		this.paymentRepository = paymentRepository;	
		this.ecRepository = ecRepository;
	}
	
	public List<ECommerce> getECommerces() {				
		List<ECommerce> eCommerces = (List<ECommerce>) ecRepository.findAll();
		if(eCommerces.isEmpty()) throw new NoResultsFoundException();
		return eCommerces;		
	}
	
	public ECommerce getECommerce(String id) {
		return ecRepository.findById(id).orElseThrow(() -> new ECommerceNotFoundException());
	}	
	
	@Transactional
	public void saveECommerce(String id) {		
		ecRepository.save(new ECommerce(id));		
	}
	
	public SettlementDto getSettlement(String eCommerceId, String ppId, int year, int month)
	{
		Utils.checkDate(year, month);
		ppRepository.findById(ppId).orElseThrow(() -> new PaymentProcessorNotFoundException());
		ecRepository.findById(eCommerceId).orElseThrow(() -> new ECommerceNotFoundException());
		//Calculate range of days in whole month with convenience methods
		LocalDate startLocalDate = LocalDate.of(year, month, 1);
		LocalDate endLocalDate = startLocalDate.plusMonths(1).minusDays(1);
		//Convert start and end dates to suitable format for repository 
		Date startDate = java.sql.Date.valueOf(startLocalDate);
		Date endDate = java.sql.Date.valueOf(endLocalDate);
		//Generate date string in format "MONTH_NAME YYYY" for billingDto object
		String monthString = startLocalDate.getMonth() + " " + startLocalDate.getYear();
		SettlementDto settlementDto = null;
		
		//Find all payments processed by PaymentProcessor for the given ECommerce and month
		List<Payment> payments = paymentRepository.findByPaymentProcessorIdAndEcommerceIdAndPaymentDateBetween(
				ppId, eCommerceId, startDate, endDate);
		
		if(payments.isEmpty()) throw new NoResultsFoundException();
		log.info("Payments in settlement of ECommerce " +  " in month " + year + "-" + month 
					  + " for PaymentProcessor " + ppId + ":\n " + payments.toString());
			
		// Count payments grouping them by amount
		Map<Double, Long> transactionsPerAmount = payments.stream()
				.collect(Collectors.groupingBy(Payment::getAmount, Collectors.counting()));

		settlementDto = new SettlementDto(eCommerceId, ppId, monthString, transactionsPerAmount);
		log.info("SettlementDto returned:\n " + settlementDto);
		
		return settlementDto;
	}
	
}
