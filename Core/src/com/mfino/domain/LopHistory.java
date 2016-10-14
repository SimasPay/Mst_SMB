package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.mfino.hibernate.Timestamp;

/**
 * LopHistory generated by hbm2java
 */
@Entity
@Table(name = "LOP_HISTORY")
public class LopHistory  extends Base implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private LetterOfPurchase letterOfPurchase;
	private BigDecimal olddiscount;
	private BigDecimal newdiscount;
	private String discountchangedby;
	private Timestamp discountchangetime;
	private String comments;

	public LopHistory() {
	}

	
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOPID", nullable = false)
	public LetterOfPurchase getLetterOfPurchase() {
		return this.letterOfPurchase;
	}

	public void setLetterOfPurchase(LetterOfPurchase letterOfPurchase) {
		this.letterOfPurchase = letterOfPurchase;
	}

	

	@Column(name = "OLDDISCOUNT", precision = 25, scale = 4)
	public BigDecimal getOlddiscount() {
		return this.olddiscount;
	}

	public void setOlddiscount(BigDecimal olddiscount) {
		this.olddiscount = olddiscount;
	}

	@Column(name = "NEWDISCOUNT", precision = 25, scale = 4)
	public BigDecimal getNewdiscount() {
		return this.newdiscount;
	}

	public void setNewdiscount(BigDecimal newdiscount) {
		this.newdiscount = newdiscount;
	}

	@Column(name = "DISCOUNTCHANGEDBY", length = 1020)
	public String getDiscountchangedby() {
		return this.discountchangedby;
	}

	public void setDiscountchangedby(String discountchangedby) {
		this.discountchangedby = discountchangedby;
	}

	@Type(type = "userDefinedTimeStamp")
	@Column(name = "DISCOUNTCHANGETIME")
	public Timestamp getDiscountchangetime() {
		return this.discountchangetime;
	}

	public void setDiscountchangetime(Timestamp discountchangetime) {
		this.discountchangetime = discountchangetime;
	}

	@Column(name = "COMMENTS", length = 1020)
	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
