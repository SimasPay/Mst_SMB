package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.mfino.hibernate.Timestamp;

/**
 * TransactionLog generated by hbm2java
 */
@Entity
@Table(name = "TRANSACTION_LOG")
public class TransactionLog  extends Base implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	public static final String FieldName_LastUpdateTime = "lastupdatetime";
	public static final String FieldName_TransactionTime = "transactiontime";
	public static final String FieldName_ParentTransactionID = "parenttransactionid";
	
	
	private MfinoServiceProvider mfinoServiceProvider;
	private BigDecimal parenttransactionid;
	private Long multixid;
	private Timestamp transactiontime;
	private long messagecode;
	private String messagedata;
	private Set<CommodityTransfer> commodityTransfers = new HashSet<CommodityTransfer>(
			0);
	private Set<PendingCommodityTransfer> pendingCommodityTransfers = new HashSet<PendingCommodityTransfer>(
			0);
	private Set<LetterOfPurchase> letterOfPurchases = new HashSet<LetterOfPurchase>(
			0);

	private Long id;
	
	
	public TransactionLog() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
	@SequenceGenerator(name = "id_Sequence", sequenceName = "transaction_log_ID_SEQ")
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MSPID", nullable = false)
	public MfinoServiceProvider getMfinoServiceProvider() {
		return this.mfinoServiceProvider;
	}

	public void setMfinoServiceProvider(
			MfinoServiceProvider mfinoServiceProvider) {
		this.mfinoServiceProvider = mfinoServiceProvider;
	}


	@Column(name = "PARENTTRANSACTIONID", scale = 0)
	public BigDecimal getParenttransactionid() {
		return this.parenttransactionid;
	}

	public void setParenttransactionid(BigDecimal parenttransactionid) {
		this.parenttransactionid = parenttransactionid;
	}

	@Column(name = "MULTIXID", precision = 10, scale = 0)
	public Long getMultixid() {
		return this.multixid;
	}

	public void setMultixid(Long multixid) {
		this.multixid = multixid;
	}

	@Type(type = "userDefinedTimeStamp")
	@Column(name = "TRANSACTIONTIME", nullable = false)
	public Timestamp getTransactiontime() {
		return this.transactiontime;
	}

	public void setTransactiontime(Timestamp transactiontime) {
		this.transactiontime = transactiontime;
	}

	@Column(name = "MESSAGECODE", nullable = false, precision = 10, scale = 0)
	public long getMessagecode() {
		return this.messagecode;
	}

	public void setMessagecode(long messagecode) {
		this.messagecode = messagecode;
	}

	@Column(name = "MESSAGEDATA", nullable = false)
	public String getMessagedata() {
		return this.messagedata;
	}

	public void setMessagedata(String data) {
		this.messagedata = data;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transactionLog")
	public Set<CommodityTransfer> getCommodityTransfers() {
		return this.commodityTransfers;
	}

	public void setCommodityTransfers(Set<CommodityTransfer> commodityTransfers) {
		this.commodityTransfers = commodityTransfers;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transactionLog")
	public Set<PendingCommodityTransfer> getPendingCommodityTransfers() {
		return this.pendingCommodityTransfers;
	}

	public void setPendingCommodityTransfers(
			Set<PendingCommodityTransfer> pendingCommodityTransfers) {
		this.pendingCommodityTransfers = pendingCommodityTransfers;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "transactionLog")
	public Set<LetterOfPurchase> getLetterOfPurchases() {
		return this.letterOfPurchases;
	}

	public void setLetterOfPurchases(Set<LetterOfPurchase> letterOfPurchases) {
		this.letterOfPurchases = letterOfPurchases;
	}

}
