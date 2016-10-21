package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * MfsLedger generated by hbm2java
 */
@Entity
@Table(name = "MFS_LEDGER")
public class MfsLedger extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String FieldName_PocketID = "pocketid";
	public static final String FieldName_CommodityTransferID = "commoditytransferid";
	public static final String FieldName_LedgerStatus = "ledgerstatus";
	public static final String FieldName_SctlId = "sctlid";
	
	private Long sctlid;
	private Long commoditytransferid;
	private Long pocketid;
	private BigDecimal amount;
	private String ledgertype;
	private String ledgerstatus;
	private Long id;

	public MfsLedger() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
	@SequenceGenerator(name = "id_Sequence", sequenceName = "mfs_ledger_ID_SEQ")
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "SCTLID", nullable = false, scale = 0)
	public Long getSctlid() {
		return this.sctlid;
	}

	public void setSctlid(Long sctlid) {
		this.sctlid = sctlid;
	}

	@Column(name = "COMMODITYTRANSFERID", nullable = false, scale = 0)
	public Long getCommoditytransferid() {
		return this.commoditytransferid;
	}

	public void setCommoditytransferid(Long commoditytransferid) {
		this.commoditytransferid = commoditytransferid;
	}

	@Column(name = "POCKETID", nullable = false, scale = 0)
	public Long getPocketid() {
		return this.pocketid;
	}

	public void setPocketid(Long pocketid) {
		this.pocketid = pocketid;
	}

	@Column(name = "AMOUNT", nullable = false, precision = 25, scale = 4)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "LEDGERTYPE", nullable = false, length = 25)
	public String getLedgertype() {
		return this.ledgertype;
	}

	public void setLedgertype(String ledgertype) {
		this.ledgertype = ledgertype;
	}

	@Column(name = "LEDGERSTATUS", nullable = false, length = 25)
	public String getLedgerstatus() {
		return this.ledgerstatus;
	}

	public void setLedgerstatus(String ledgerstatus) {
		this.ledgerstatus = ledgerstatus;
	}

}
