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

import com.epg.challenge.dto.SettlementDto;
import com.epg.challenge.entity.ECommerce;
import com.epg.challenge.entity.Payment;
import com.epg.challenge.entity.PaymentProcessor;
import com.epg.challenge.exception.Constants;
import com.epg.challenge.exception.ECommerceNotFoundException;
import com.epg.challenge.exception.InvalidDateException;
import com.epg.challenge.exception.NoResultsFoundException;
import com.epg.challenge.exception.PaymentProcessorNotFoundException;
import com.epg.challenge.repository.ECommerceRepository;
import com.epg.challenge.repository.PaymentProcessorRepository;
import com.epg.challenge.repository.PaymentRepository;
import com.epg.challenge.service.ECommerceService;
import com.epg.challenge.util.Utils;

@ExtendWith(MockitoExtension.class)
class ECommerceServiceTest {
	
	@InjectMocks
	ECommerceService eCommerceService;

	@Mock PaymentProcessorRepository ppRepository;
	@Mock PaymentRepository paymentRepository;
	@Mock ECommerceRepository ecRepository;

	/*
	@Test	
	void test() {
		fail("Not yet implemented");
	}*/
	
	@Test
	void getECommerce_Should_ThrowECommerceNotFoundException_When_ECommerceDoesNotExist() {
		when(ecRepository.findById("ECOMMERCE1")).thenReturn(Optional.empty());		
		assertThrows(ECommerceNotFoundException.class, 
					() -> eCommerceService.getECommerce("ECOMMERCE1"));
	}
			
	
	@Test
	void getECommerces_Should_ThrowNoResultsFound_When_NoECommerces() {
		when(ecRepository.findAll()).thenReturn(Collections.emptyList());
		assertThrows(NoResultsFoundException.class, 
				() -> eCommerceService.getECommerces());
	}

	
	@ParameterizedTest
	@ValueSource(ints = {-1, 0, 13})
	void getSettlement_Should_ThrowInvalidDateException_When_MonthIsInvalid(int month) {				
		Throwable t = assertThrows(InvalidDateException.class, 
								  () -> eCommerceService.getSettlement("ECOMMERCE1", "CCPP1", 2021, month));
		assertTrue(t.getMessage().equals(Constants.INVALID_MONTH));		
	}
	
	@Test	
	void getSettlement_Should_ThrowInvalidDateException_When_YearIsInvalid() {				
		Throwable t = assertThrows(InvalidDateException.class, 
								   () -> eCommerceService.getSettlement("ECOMMERCE1", "CCPP1", -1, 10));
		assertTrue(t.getMessage().equals(Constants.INVALID_YEAR));
	}
	
	@Test	
	void getSettlement_Should_ThrowInvalidDateException_When_DateIsFuture() {				
		Throwable t = assertThrows(InvalidDateException.class, 
								   () -> eCommerceService.getSettlement("ECOMMERCE1", "CCPP1", 2022, 12));
		assertTrue(t.getMessage().equals(Constants.DATE_MUST_NOT_BE_FUTURE));
	}

	
	
	@Test
	void getSettlement_Should_ThrowPaymentProcessorNotFoundException_When_PaymentProcessorDoesNotExist() {				
		when(ppRepository.findById("CCPP1")).thenReturn(Optional.empty());				

		assertThrows(PaymentProcessorNotFoundException.class, 
					() -> eCommerceService.getSettlement("ECOMMERCE1", "CCPP1", 2021, 11));
				
	}
	
	
	@Test
	void getSettlement_Should_ThrowECommerceNotFoundException_When_ECommerceDoesNotExist() {
		PaymentProcessor pp = new PaymentProcessor();
		pp.setId("CCPP1");
		
		when(ppRepository.findById("CCPP1")).thenReturn(Optional.of(pp));
		when(ecRepository.findById("ECOMMERCE1")).thenReturn(Optional.empty());		

		assertThrows(ECommerceNotFoundException.class, 
						() -> eCommerceService.getSettlement("ECOMMERCE1", "CCPP1", 2021, 11));				
	}
	
	
	@Test
	void getSettlement_Should_ThrowNoResultsFound_When_NoPaymentsExist() {
		PaymentProcessor pp = new PaymentProcessor();
		pp.setId("CCPP1");
		ECommerce eCommerce = new ECommerce();
		eCommerce.setId("ECOMMERCE1");
		
		when(ppRepository.findById("CCPP1")).thenReturn(Optional.of(pp));
		when(ecRepository.findById("ECOMMERCE1")).thenReturn(Optional.of(eCommerce));
		when(paymentRepository.findByPaymentProcessorIdAndEcommerceIdAndPaymentDateBetween(
				"CCPP1", 
				"ECOMMERCE1", 
				java.sql.Date.valueOf(LocalDate.of(2021, 11, 1)), 
				java.sql.Date.valueOf(LocalDate.of(2021, 11, 30)))).thenReturn(Collections.emptyList());
		
		assertThrows(NoResultsFoundException.class, 
						() -> eCommerceService.getSettlement("ECOMMERCE1", "CCPP1", 2021, 11));				
	}
	
		
	@Test
	void getSettlement_Should_ReturnCorrectSettlementDto_When_InputsAreCorrect() {
		PaymentProcessor pp = new PaymentProcessor();
		pp.setId("CCPP1");
		ECommerce eCommerce = new ECommerce();
		eCommerce.setId("ECOMMERCE1");
		
		List<Payment> payments = new ArrayList<>();
		payments.add(TestUtils.createPayment(1, "CCPP1", "ECOMMERCE1", LocalDate.of(2021, 11, 1), 5.0));
		payments.add(TestUtils.createPayment(2, "CCPP1", "ECOMMERCE1", LocalDate.of(2021, 11, 2), 3.0));
		payments.add(TestUtils.createPayment(3, "CCPP1", "ECOMMERCE1", LocalDate.of(2021, 11, 5), 3.0));
		payments.add(TestUtils.createPayment(4, "CCPP1", "ECOMMERCE1", LocalDate.of(2021, 11, 8), 7.0));
		payments.add(TestUtils.createPayment(5, "CCPP1", "ECOMMERCE1", LocalDate.of(2021, 11, 9), 5.0));
		payments.add(TestUtils.createPayment(6, "CCPP1", "ECOMMERCE1", LocalDate.of(2021, 11, 14), 5.0));
		
		when(ppRepository.findById("CCPP1")).thenReturn(Optional.of(pp));
		when(ecRepository.findById("ECOMMERCE1")).thenReturn(Optional.of(eCommerce));
		when(paymentRepository.findByPaymentProcessorIdAndEcommerceIdAndPaymentDateBetween(
				"CCPP1", 
				"ECOMMERCE1", 
				java.sql.Date.valueOf(LocalDate.of(2021, 11, 1)), 
				java.sql.Date.valueOf(LocalDate.of(2021, 11, 30)))).thenReturn(payments);
	
		SettlementDto settlementDto = eCommerceService.getSettlement("ECOMMERCE1", "CCPP1", 2021, 11);
		
		assertAll(
			() -> assertTrue(settlementDto.geteCommerceId().equals("ECOMMERCE1")),
			() -> assertTrue(settlementDto.getPaymentProcessorId().equals("CCPP1")),
			() -> assertTrue(settlementDto.getMonth().equals("NOVEMBER 2021")),
			() -> assertTrue(settlementDto.getTransactionsPerAmount().get(5.0) == 3),
			() -> assertTrue(settlementDto.getTransactionsPerAmount().get(3.0) == 2),
			() -> assertTrue(settlementDto.getTransactionsPerAmount().get(7.0) == 1)
		);					
	}	

}
