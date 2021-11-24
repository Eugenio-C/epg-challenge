package com.epg.challenge.entity;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;


@Entity
public class PaymentProcessor {
	
	@Id
	private String id;
	private double flatFee;
		
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		   name="ACQUIRER_PLUS_PRICING",
 		   joinColumns=@JoinColumn(name = "PAYMENT_PROCESSOR_ID", referencedColumnName = "ID")
	)
	private List<AcquirerPlusPricing> acquirerPlusPricingList;
	
	
	public double getFlatFee() {
		return flatFee;
	}
	public void setFlatFee(double flatFee) {
		this.flatFee = flatFee;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public List<AcquirerPlusPricing> getAcquirerPlusPricingList() {
		return acquirerPlusPricingList;
	}
	public void setAcquirerPlusPricingList(List<AcquirerPlusPricing> acquirerPlusPricingList) {
		this.acquirerPlusPricingList = acquirerPlusPricingList;
	}
	
	@Override
	public String toString() {
		return "PaymentProcessor(Id = " + this.id + ", Flat Fee = " + flatFee + 
				", APPs = " + this.acquirerPlusPricingList.toString() + ")";
	}
	
}
