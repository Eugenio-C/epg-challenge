package com.epg.challenge.repository;

import org.springframework.data.repository.CrudRepository;

import com.epg.challenge.entity.PaymentProcessor;

public interface PaymentProcessorRepository extends CrudRepository<PaymentProcessor, String> {

}
