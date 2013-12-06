package com.mfino.dao.query;

import java.math.BigDecimal;
/**
 * @author Satya
 *
 */
public class MoneyClearanceGravedQuery extends BaseQuery {
	private Integer status;
	private Long mdnId ;
	private Long pocketId ;
	private Long sctlId ;
	private BigDecimal amount;
	private Long refundMdnId;
	private String refundAccountNumber;
	private Long refundPocketId;
	private Long refundSctlId;
	private Integer[] multiStatus;
	private boolean restrictionIsEquals = true;
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getMdnId() {
		return mdnId;
	}
	public void setMdnId(Long mdnId) {
		this.mdnId = mdnId;
	}
	public Long getPocketId() {
		return pocketId;
	}
	public void setPocketId(Long pocketId) {
		this.pocketId = pocketId;
	}
	public Long getSctlId() {
		return sctlId;
	}
	public void setSctlId(Long sctlId) {
		this.sctlId = sctlId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Long getRefundMdnId() {
		return refundMdnId;
	}
	public void setRefundMdnId(Long refundMdnId) {
		this.refundMdnId = refundMdnId;
	}
	public String getRefundAccountNumber() {
		return refundAccountNumber;
	}
	public void setRefundAccountNumber(String refundAccountNumber) {
		this.refundAccountNumber = refundAccountNumber;
	}
	public Long getRefundPocketId() {
		return refundPocketId;
	}
	public void setRefundPocketId(Long refundPocketId) {
		this.refundPocketId = refundPocketId;
	}
	public Long getRefundSctlId() {
		return refundSctlId;
	}
	public void setRefundSctlId(Long refundSctlId) {
		this.refundSctlId = refundSctlId;
	}
	public Integer[] getMultiStatus() {
		return multiStatus;
	}
	public void setMultiStatus(Integer[] multiStatus) {
		this.multiStatus = multiStatus;
	}
	public boolean isRestrictionIsEquals() {
		return restrictionIsEquals;
	}
	public void setRestrictionIsEquals(boolean restrictionIsEquals) {
		this.restrictionIsEquals = restrictionIsEquals;
	}
	
}
