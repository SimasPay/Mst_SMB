package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * DbParam generated by hbm2java
 */
@Entity
@Table(name = "DB_PARAM")
public class DbParam extends Base implements java.io.Serializable {

	
	private String name;
	private String value;

	public DbParam() {
	}

	@Column(name = "NAME", length = 1020)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "VALUE", length = 1020)
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
