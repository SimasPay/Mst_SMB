/**
 * 
 */
package com.mfino.dao.query;

import java.util.Date;

/**
 * @author Bala Sunku
 *
 */
public class TransactionChargeQuery extends BaseQuery {
	
	private Long transactionRuleId;
	private Long chargeTypeId;
	private Long chargeDefinitionId;
	private Date startDate;
	private Date endDate;
	
	public Long getTransactionRuleId() {
		return transactionRuleId;
	}
	public void setTransactionRuleId(Long transactionRuleId) {
		this.transactionRuleId = transactionRuleId;
	}
	public Long getChargeTypeId() {
		return chargeTypeId;
	}
	public void setChargeTypeId(Long chargeTypeId) {
		this.chargeTypeId = chargeTypeId;
	}
	public Long getChargeDefinitionId() {
		return chargeDefinitionId;
	}
	public void setChargeDefinitionId(Long chargeDefinitionId) {
		this.chargeDefinitionId = chargeDefinitionId;
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
