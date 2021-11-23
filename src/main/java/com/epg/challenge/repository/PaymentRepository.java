package com.epg.challenge.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.epg.challenge.entity.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
	List<Payment> findByPaymentProcessorIdAndEcommerceIdAndPaymentDateBetween(
						String paymentProcessorId, String eCommerceId, Date startDate, Date endDate);
	
	List<Payment> findByPaymentProcessorIdAndPaymentDateBetween(
						String paymentProcessorId, Date startDate, Date endDate);

}
