package com.mfino.dao.query;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Srikanth
 * 
 */
public class AdjustmentsQuery extends BaseQuery {
	private Long sctlID;
	private Long sourcePocketID;
	private Long destPocketID;
	private BigDecimal amount;
	private Integer adjustmentStatus;
	private Date startDate;
	private Date endDate;

	public Long getSctlID() {
		return sctlID;
	}

	public void setSctlID(Long sctlID) {
		this.sctlID = sctlID;
	}

	public Long getSourcePocketID() {
		return sourcePocketID;
	}

	public void setSourcePocketID(Long sourcePocketID) {
		this.sourcePocketID = sourcePocketID;
	}

	public Long getDestPocketID() {
		return destPocketID;
	}

	public void setDestPocketID(Long destPocketID) {
		this.destPocketID = destPocketID;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getAdjustmentStatus() {
		return adjustmentStatus;
	}

	public void setAdjustmentStatus(Integer adjustmentStatus) {
		this.adjustmentStatus = adjustmentStatus;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
