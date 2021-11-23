package com.epg.challenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epg.challenge.dto.SettlementDto;
import com.epg.challenge.entity.ECommerce;
import com.epg.challenge.service.ECommerceService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/ECommerces")
public class ECommerceController {

	private final ECommerceService eCommerceService;
	
	@Autowired
	public ECommerceController(ECommerceService eCommerceService) {
		this.eCommerceService = eCommerceService;
	}
	
	@GetMapping()
	@ApiOperation("Get all the ECommerces available")
	public List<ECommerce> getECommerces() {				
		return eCommerceService.getECommerces();
	}
	
	
	@GetMapping("/{id}")
	@ApiOperation("Get ECommerce info")
	public ECommerce getECommerce(
			@ApiParam("ECommerce id") @PathVariable("id") String ecId) {
		return eCommerceService.getECommerce(ecId);
	}
	
	
	@PutMapping("/{id}")
	@ApiOperation("Create or update ECommerce")
	public void putECommerce(
			@ApiParam("ECommerce id") @PathVariable("id") String ecId) {			
		eCommerceService.saveECommerce(ecId);		
	}
	
	

	@GetMapping("/{id}/SettlementFor/{paymentProcessorId}/{year}/{month}")
	@ApiOperation("Obtain settlement from Ecommerce {id} for PaymentProcessor {paymentProcessorId} for date {year}/{month}")
	public SettlementDto getSettlement(
			@ApiParam("ECommerce id") @PathVariable("id") String eCommerceId,
			@ApiParam("PaymentProcessor id") @PathVariable("paymentProcessorId") String ppId,			
			@ApiParam("Year (YYYY)") @PathVariable("year") int year,
			@ApiParam("Month (MM)") @PathVariable("month") int month)
	{		
		return eCommerceService.getSettlement(eCommerceId, ppId, year, month);
	}
	
	

}
