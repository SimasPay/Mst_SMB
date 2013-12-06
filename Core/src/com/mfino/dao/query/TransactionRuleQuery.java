/**
 * 
 */
package com.mfino.dao.query;

import java.util.Date;

/**
 * @author Bala Sunku
 *
 */
public class TransactionRuleQuery extends BaseQuery {
	
	private String ExactName, Name;
	private Date startDate, endDate;
	private Long serviceProviderId, serviceId, transactionTypeId, channelCodeId, sourceKYC, destKYC;
	private Integer chargeMode, sourceType, destType;
	private Long sourceGroup;
	private Long destinationGroup;
	private boolean exactMatch;
	
	public String getExactName() {
		return ExactName;
	}
	
	public void setExactName(String exactName) {
		ExactName = exactName;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
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
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public Long getTransactionTypeId() {
		return transactionTypeId;
	}
	public void setTransactionTypeId(Long transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}
	public Long getChannelCodeId() {
		return channelCodeId;
	}
	public void setChannelCodeId(Long channelCodeId) {
		this.channelCodeId = channelCodeId;
	}
	public Integer getChargeMode() {
		return chargeMode;
	}
	public void setChargeMode(Integer chargeMode) {
		this.chargeMode = chargeMode;
	}
	public Long getServiceProviderId() {
		return serviceProviderId;
	}
	public void setServiceProviderId(Long serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}
	public Integer getSourceType() {
		return sourceType;
	}
	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}
	public Long getSourceKYC() {
		return sourceKYC;
	}
	public void setSourceKYC(Long sourceKYC) {
		this.sourceKYC = sourceKYC;
	}
	public Integer getDestType() {
		return destType;
	}
	public void setDestType(Integer destType) {
		this.destType = destType;
	}
	public Long getDestKYC() {
		return destKYC;
	}
	public void setDestKYC(Long destKYC) {
		this.destKYC = destKYC;
	}
	public Long getSourceGroup() {
		return sourceGroup;
	}
	public void setSourceGroup(Long sourceGroup) {
		this.sourceGroup = sourceGroup;
	}
	
	public Long getDestinationGroup() {
		return destinationGroup;
	}
	
	public void setDestinationGroup(Long destinationGroup) {
		this.destinationGroup = destinationGroup;
	}
	
	public boolean isExactMatch() {
		return exactMatch;
	}
	
	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}
}
