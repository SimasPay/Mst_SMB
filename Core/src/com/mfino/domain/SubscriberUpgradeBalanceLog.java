package com.mfino.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.mfino.hibernate.Timestamp;

@Entity
@Table(name = "SUBSCRIBER_UPGRADE_BALANCE_LOG")
public class SubscriberUpgradeBalanceLog {
	private Long id;
	private Long subscriberId;
	private BigDecimal pockatBalance;
	private Timestamp txnDate;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
	@SequenceGenerator(name = "id_Sequence", sequenceName = "SUBSUPGRADE_BALANCELOG_ID_SEQ")
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "SUBSCRIBERID")
	public Long getSubscriberId() {
		return subscriberId;
	}
	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}
	
	@Type(type = "encryptedBigDecimal")
	@Column(name = "POCKET_BALANCE", length = 1020)
	public BigDecimal getPockatBalance() {
		return pockatBalance;
	}
	public void setPockatBalance(BigDecimal pockatBalance) {
		this.pockatBalance = pockatBalance;
	}
	
	@Type(type = "userDefinedTimeStamp")
	@Column(name = "TXNDATE")
	public Timestamp getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(Timestamp txnDate) {
		this.txnDate = txnDate;
	}
	
}
