package com.epg.challenge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epg.challenge.dto.BillingDto;
import com.epg.challenge.dto.SettlementDto;
import com.epg.challenge.entity.AcquirerPlusPricing;
import com.epg.challenge.entity.ECommerce;
import com.epg.challenge.entity.Payment;
import com.epg.challenge.entity.PaymentProcessor;
import com.epg.challenge.exception.Constants;
import com.epg.challenge.exception.ECommerceNotFoundException;
import com.epg.challenge.exception.InvalidAmountException;
import com.epg.challenge.exception.InvalidDateException;
import com.epg.challenge.exception.NoResultsFoundException;
import com.epg.challenge.exception.PaymentProcessorNotFoundException;
import com.epg.challenge.repository.ECommerceRepository;
import com.epg.challenge.repository.PaymentProcessorRepository;
import com.epg.challenge.repository.PaymentRepository;
import com.epg.challenge.service.ECommerceService;
import com.epg.challenge.service.PaymentProcessorService;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorServiceTest {
	
	@InjectMocks
	PaymentProcessorService paymentProcessorService;

	@Mock PaymentProcessorRepository ppRepository;
	@Mock PaymentRepository paymentRepository;
	@Mock ECommerceRepository ecRepository;

	@Test
	void getPaymentProcessor_Should_ThrowPaymentProcessorNotFoundException_When_PaymentProcessorDoesNotExist() {
		when(ppRepository.findById("CCPP1")).thenReturn(Optional.empty());		
		assertThrows(PaymentProcessorNotFoundException.class, 
					() -> paymentProcessorService.getPaymentProcessor("CCPP1"));
	}
			
	
	@Test
	void getPaymentProcessors_Should_ThrowNoResultsFound_When_NoPaymentProcessors() {
		when(ppRepository.findAll()).thenReturn(Collections.emptyList());
		assertThrows(NoResultsFoundException.class, 
				() -> paymentProcessorService.getPaymentProcessors());
	}

	
	
	
	
	
	@Test
	void processPayment_Should_ThrowInvalidAmountException_When_AmountIsInvalid() {				
		assertThrows(InvalidAmountException.class, 
					() -> paymentProcessorService.processPayment("CCPP1", "ECOMMERCE1", -1));				
	}
		
	
	@Test
	void processPayment_Should_ThrowPaymentProcessorNotFoundException_When_PaymentProcessorDoesNotExist() {				
		when(ppRepository.findById("CCPP1")).thenReturn(Optional.empty());				

		assertThrows(PaymentProcessorNotFoundException.class, 
					() -> paymentProcessorService.processPayment("CCPP1", "ECOMMERCE1", 5));				
	}
	
	
	@Test
	void processPayment_Should_ThrowECommerceNotFoundException_When_ECommerceDoesNotExist() {
		PaymentProcessor pp = new PaymentProcessor();
		pp.setId("CCPP1");
		
		when(ppRepository.findById("CCPP1")).thenReturn(Optional.of(pp));
		when(ecRepository.findById("ECOMMERCE1")).thenReturn(Optional.empty());		

		assertThrows(ECommerceNotFoundException.class, 
						() -> paymentProcessorService.processPayment("CCPP1", "ECOMMERCE1", 5));				
	}
	
	
	@ParameterizedTest
	@ValueSource(ints = {-1, 0, 13})
	void getBilling_Should_ThrowInvalidDateException_When_MonthIsInvalid(int month) {				
		Throwable t = assertThrows(InvalidDateException.class, 
								  () -> paymentProcessorService.getBilling("CCPP1", 2021, month));
		assertTrue(t.getMessage().equals(Constants.INVALID_MONTH));		
	}
	
	@Test	
	void getBilling_Should_ThrowInvalidDateException_When_YearIsInvalid() {				
		Throwable t = assertThrows(InvalidDateException.class, 
								   () -> paymentProcessorService.getBilling("CCPP1", -1, 11));
		assertTrue(t.getMessage().equals(Constants.INVALID_YEAR));
	}
	
	@Test	
	void getBilling_Should_ThrowInvalidDateException_When_DateIsFuture() {				
		Throwable t = assertThrows(InvalidDateException.class, 
								   () -> paymentProcessorService.getBilling("CCPP1", 2022, 12));
		assertTrue(t.getMessage().equals(Constants.DATE_MUST_NOT_BE_FUTURE));
	}
	
	@Test
	void getBilling_Should_ThrowPaymentProcessorNotFoundException_When_PaymentProcessorDoesNotExist() {				
		when(ppRepository.findById("CCPP1")).thenReturn(Optional.empty());				

		assertThrows(PaymentProcessorNotFoundException.class, 
					() -> paymentProcessorService.getBilling("CCPP1", 2021, 11));
				
	}
	
	@Test
	void getBilling_Should_ThrowNoResultsFound_When_NoPaymentsExist() {
		PaymentProcessor pp = new PaymentProcessor();
		pp.setId("CCPP1");		
		
		when(ppRepository.findById("CCPP1")).thenReturn(Optional.of(pp));		
		when(paymentRepository.findByPaymentProcessorIdAndPaymentDateBetween(
				"CCPP1",
				java.sql.Date.valueOf(LocalDate.of(2021, 11, 1)), 
				java.sql.Date.valueOf(LocalDate.of(2021, 11, 30)))).thenReturn(Collections.emptyList());
		
		assertThrows(NoResultsFoundException.class, 
						() -> paymentProcessorService.getBilling("CCPP1", 2021, 11));				
	}
	
	
	@Test
	void getBilling_Should_ReturnCorrectBillingDto_When_InputsAreCorrect() {
		//Set up PaymentProcessor
		List<AcquirerPlusPricing> acquirerPlusPricingList = new ArrayList<>();
		acquirerPlusPricingList.add(new AcquirerPlusPricing(2, 2));
		acquirerPlusPricingList.add(new AcquirerPlusPricing(5, 1.5));
		
		PaymentProcessor pp = new PaymentProcessor();
		pp.setId("CCPP1");
		pp.setFlatFee(0.2);
		pp.setAcquirerPlusPricingList(acquirerPlusPricingList);
		
		//Set up Payments
		List<Payment> payments = new ArrayList<>();
		payments.add(TestUtils.createPayment(1, "CCPP1", "ECOMMERCE1", LocalDate.of(2021, 11, 1), 5.0));
		payments.add(TestUtils.createPayment(2, "CCPP1", "ECOMMERCE2", LocalDate.of(2021, 11, 1), 6.0));
		payments.add(TestUtils.createPayment(3, "CCPP1", "ECOMMERCE2", LocalDate.of(2021, 11, 2), 4.0));
		payments.add(TestUtils.createPayment(4, "CCPP1", "ECOMMERCE2", LocalDate.of(2021, 11, 3), 6.0));
		payments.add(TestUtils.createPayment(5, "CCPP1", "ECOMMERCE2", LocalDate.of(2021, 11, 4), 4.0));
		payments.add(TestUtils.createPayment(6, "CCPP1", "ECOMMERCE2", LocalDate.of(2021, 11, 5), 2.0));
		payments.add(TestUtils.createPayment(7, "CCPP1", "ECOMMERCE2", LocalDate.of(2021, 11, 6), 2.0));

		
		//Setup Repositories		
		when(ppRepository.findById("CCPP1")).thenReturn(Optional.of(pp));		
		when(paymentRepository.findByPaymentProcessorIdAndPaymentDateBetween(
				"CCPP1",
				java.sql.Date.valueOf(LocalDate.of(2021, 11, 1)), 
				java.sql.Date.valueOf(LocalDate.of(2021, 11, 30)))).thenReturn(payments);
		
		BillingDto billingDto = paymentProcessorService.getBilling("CCPP1", 2021, 11);						
		
		assertAll(
			() -> assertTrue(billingDto.getPaymentProcessorId().equals("CCPP1")),			
			() -> assertTrue(billingDto.getMonth().equals("NOVEMBER 2021")),
			() -> assertTrue(billingDto.getBillingPerECommerce().get("ECOMMERCE1") == 5.3),
			() -> assertTrue(billingDto.getBillingPerECommerce().get("ECOMMERCE2") == 25.68)
		);	
	}	

}
