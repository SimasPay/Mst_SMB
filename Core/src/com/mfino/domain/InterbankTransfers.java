package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * InterbankTransfers generated by hbm2java
 */
@Entity
@Table(name = "INTERBANK_TRANSFERS")
public class InterbankTransfers implements java.io.Serializable {

	private BigDecimal id;
	private long version;
	private Serializable lastupdatetime;
	private String updatedby;
	private Serializable createtime;
	private String createdby;
	private String terminalid;
	private String destbankcode;
	private String sourceaccountname;
	private String destaccountname;
	private String sourceaccountnumber;
	private String destaccountnumber;
	private BigDecimal amount;
	private BigDecimal charges;
	private BigDecimal transferid;
	private BigDecimal sctlid;
	private String sessionid;
	private String narration;
	private String paymentreference;
	private String nibresponsecode;
	private Long ibtstatus;
	private String destbankname;

	public InterbankTransfers() {
	}

	public InterbankTransfers(BigDecimal id, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby,
			String destbankcode, BigDecimal amount, BigDecimal charges) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.destbankcode = destbankcode;
		this.amount = amount;
		this.charges = charges;
	}

	public InterbankTransfers(BigDecimal id, Serializable lastupdatetime,
			String updatedby, Serializable createtime, String createdby,
			String terminalid, String destbankcode, String sourceaccountname,
			String destaccountname, String sourceaccountnumber,
			String destaccountnumber, BigDecimal amount, BigDecimal charges,
			BigDecimal transferid, BigDecimal sctlid, String sessionid,
			String narration, String paymentreference, String nibresponsecode,
			Long ibtstatus, String destbankname) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.terminalid = terminalid;
		this.destbankcode = destbankcode;
		this.sourceaccountname = sourceaccountname;
		this.destaccountname = destaccountname;
		this.sourceaccountnumber = sourceaccountnumber;
		this.destaccountnumber = destaccountnumber;
		this.amount = amount;
		this.charges = charges;
		this.transferid = transferid;
		this.sctlid = sctlid;
		this.sessionid = sessionid;
		this.narration = narration;
		this.paymentreference = paymentreference;
		this.nibresponsecode = nibresponsecode;
		this.ibtstatus = ibtstatus;
		this.destbankname = destbankname;
	}

	@Id
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 10, scale = 0)
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Column(name = "LASTUPDATETIME", nullable = false)
	public Serializable getLastupdatetime() {
		return this.lastupdatetime;
	}

	public void setLastupdatetime(Serializable lastupdatetime) {
		this.lastupdatetime = lastupdatetime;
	}

	@Column(name = "UPDATEDBY", nullable = false, length = 1020)
	public String getUpdatedby() {
		return this.updatedby;
	}

	public void setUpdatedby(String updatedby) {
		this.updatedby = updatedby;
	}

	@Column(name = "CREATETIME", nullable = false)
	public Serializable getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Serializable createtime) {
		this.createtime = createtime;
	}

	@Column(name = "CREATEDBY", nullable = false, length = 1020)
	public String getCreatedby() {
		return this.createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	@Column(name = "TERMINALID", length = 1020)
	public String getTerminalid() {
		return this.terminalid;
	}

	public void setTerminalid(String terminalid) {
		this.terminalid = terminalid;
	}

	@Column(name = "DESTBANKCODE", nullable = false, length = 1020)
	public String getDestbankcode() {
		return this.destbankcode;
	}

	public void setDestbankcode(String destbankcode) {
		this.destbankcode = destbankcode;
	}

	@Column(name = "SOURCEACCOUNTNAME", length = 1020)
	public String getSourceaccountname() {
		return this.sourceaccountname;
	}

	public void setSourceaccountname(String sourceaccountname) {
		this.sourceaccountname = sourceaccountname;
	}

	@Column(name = "DESTACCOUNTNAME", length = 1020)
	public String getDestaccountname() {
		return this.destaccountname;
	}

	public void setDestaccountname(String destaccountname) {
		this.destaccountname = destaccountname;
	}

	@Column(name = "SOURCEACCOUNTNUMBER", length = 1020)
	public String getSourceaccountnumber() {
		return this.sourceaccountnumber;
	}

	public void setSourceaccountnumber(String sourceaccountnumber) {
		this.sourceaccountnumber = sourceaccountnumber;
	}

	@Column(name = "DESTACCOUNTNUMBER", length = 1020)
	public String getDestaccountnumber() {
		return this.destaccountnumber;
	}

	public void setDestaccountnumber(String destaccountnumber) {
		this.destaccountnumber = destaccountnumber;
	}

	@Column(name = "AMOUNT", nullable = false, precision = 25, scale = 4)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "CHARGES", nullable = false, precision = 25, scale = 4)
	public BigDecimal getCharges() {
		return this.charges;
	}

	public void setCharges(BigDecimal charges) {
		this.charges = charges;
	}

	@Column(name = "TRANSFERID", scale = 0)
	public BigDecimal getTransferid() {
		return this.transferid;
	}

	public void setTransferid(BigDecimal transferid) {
		this.transferid = transferid;
	}

	@Column(name = "SCTLID", scale = 0)
	public BigDecimal getSctlid() {
		return this.sctlid;
	}

	public void setSctlid(BigDecimal sctlid) {
		this.sctlid = sctlid;
	}

	@Column(name = "SESSIONID", length = 1020)
	public String getSessionid() {
		return this.sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	@Column(name = "NARRATION", length = 1020)
	public String getNarration() {
		return this.narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	@Column(name = "PAYMENTREFERENCE", length = 1020)
	public String getPaymentreference() {
		return this.paymentreference;
	}

	public void setPaymentreference(String paymentreference) {
		this.paymentreference = paymentreference;
	}

	@Column(name = "NIBRESPONSECODE", length = 1020)
	public String getNibresponsecode() {
		return this.nibresponsecode;
	}

	public void setNibresponsecode(String nibresponsecode) {
		this.nibresponsecode = nibresponsecode;
	}

	@Column(name = "IBTSTATUS", precision = 10, scale = 0)
	public Long getIbtstatus() {
		return this.ibtstatus;
	}

	public void setIbtstatus(Long ibtstatus) {
		this.ibtstatus = ibtstatus;
	}

	@Column(name = "DESTBANKNAME")
	public String getDestbankname() {
		return this.destbankname;
	}

	public void setDestbankname(String destbankname) {
		this.destbankname = destbankname;
	}

}
