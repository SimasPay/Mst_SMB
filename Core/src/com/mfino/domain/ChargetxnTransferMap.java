package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mfino.hibernate.Timestamp;

/**
 * ChargetxnTransferMap generated by hbm2java
 */
@Entity
@Table(name = "CHARGETXN_TRANSFER_MAP")
public class ChargetxnTransferMap extends Base implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FieldName_SctlId = "sctlid";
	public static final String FieldName_CommodityTransferID = "commoditytransferid";
	
	private BigDecimal sctlid;
	private BigDecimal commoditytransferid;

	public ChargetxnTransferMap() {
	}

	

	
	@Column(name = "SCTLID", nullable = false, scale = 0)
	public BigDecimal getSctlid() {
		return this.sctlid;
	}

	public void setSctlid(BigDecimal sctlid) {
		this.sctlid = sctlid;
	}

	@Column(name = "COMMODITYTRANSFERID", nullable = false, scale = 0)
	public BigDecimal getCommoditytransferid() {
		return this.commoditytransferid;
	}

	public void setCommoditytransferid(BigDecimal commoditytransferid) {
		this.commoditytransferid = commoditytransferid;
	}

}
