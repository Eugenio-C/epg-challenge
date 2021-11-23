package com.epg.challenge.entity;

import javax.persistence.Embeddable;

@Embeddable
public class AcquirerPlusPricing {
	
	private int volume;
	private double percentage;
	
	public AcquirerPlusPricing() {}
	
	public AcquirerPlusPricing(int volume, double percentage) {
		this.volume = volume;
		this.percentage = percentage;
	}
	
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
	@Override
	public String toString() {
		return "[Volume: " + this.volume + "/day - APP: " + this.percentage + "%]";
	}

}
