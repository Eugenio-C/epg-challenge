package com.epg.challenge.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ECommerce {
	
	@Id
	private String id;
	
	public ECommerce() {}
	
	public ECommerce(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
