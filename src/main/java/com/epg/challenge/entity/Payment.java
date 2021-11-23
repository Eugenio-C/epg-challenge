package com.epg.challenge.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Payment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String paymentProcessorId;
	
	private String ecommerceId;	
	private double amount;	
    @Temporal(TemporalType.DATE)
	private Date paymentDate;
   
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPaymentProcessorId() {
		return paymentProcessorId;
	}
	public void setPaymentProcessorId(String paymentProcessorId) {
		this.paymentProcessorId = paymentProcessorId;
	}
	
	public String getEcommerceId() {
		return ecommerceId;
	}
	public void setEcommerceId(String ecommerceId) {
		this.ecommerceId = ecommerceId;
	}
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	public String toString() {
		return "Payment(id: " + id + ", PaymentProcessorId: " + paymentProcessorId + ", ECommerceId: " + ecommerceId
				+ ", Amount: " + amount + ", PaymentDate: " + paymentDate + ")";
	}

}
