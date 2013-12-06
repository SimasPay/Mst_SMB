/**
 * 
 */
package com.mfino.dao.query;

import java.util.Date;

import com.mfino.domain.ChannelCode;
import com.mfino.domain.Service;
import com.mfino.domain.TransactionType;

/**
 * @author Bala Sunku
 *
 */
public class ServiceChargeTemplateQuery extends BaseQuery {

	private Long SCTID;
	private String serviceChargeTemplateName;
	private String serviceType;
	private String transactionType;
	private String accessChannel;
	private Date startDate;
	private Date endDate;
	private String exactSCTName;
	private Service serviceObj;
	private TransactionType transactionTypeObj;
	private ChannelCode accessChannelObj;
	
	private Long serviceID;
	private Long serviceProviderID;
	private Long transactionTypeID;
	private Long channelCodeID;

	
	public Long getSCTID() {
		return SCTID;
	}

	public void setSCTID(Long sCTID) {
		SCTID = sCTID;
	}

	public String getServiceChargeTemplateName() {
		return serviceChargeTemplateName;
	}

	public void setServiceChargeTemplateName(String serviceChargeTemplateName) {
		this.serviceChargeTemplateName = serviceChargeTemplateName;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getAccessChannel() {
		return accessChannel;
	}

	public void setAccessChannel(String accessChannel) {
		this.accessChannel = accessChannel;
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

	public String getExactSCTName() {
		return exactSCTName;
	}

	public void setExactSCTName(String exactSCTName) {
		this.exactSCTName = exactSCTName;
	}

	public Service getServiceObj() {
		return serviceObj;
	}

	public void setServiceObj(Service serviceObj) {
		this.serviceObj = serviceObj;
	}

	public TransactionType getTransactionTypeObj() {
		return transactionTypeObj;
	}

	public void setTransactionTypeObj(TransactionType transactionTypeObj) {
		this.transactionTypeObj = transactionTypeObj;
	}

	public ChannelCode getAccessChannelObj() {
		return accessChannelObj;
	}

	public void setAccessChannelObj(ChannelCode accessChannelObj) {
		this.accessChannelObj = accessChannelObj;
	}

	public Long getServiceID() {
		return serviceID;
	}

	public void setServiceID(Long serviceID) {
		this.serviceID = serviceID;
	}

	public Long getServiceProviderID() {
		return serviceProviderID;
	}

	public void setServiceProviderID(Long serviceProviderID) {
		this.serviceProviderID = serviceProviderID;
	}

	public Long getTransactionTypeID() {
		return transactionTypeID;
	}

	public void setTransactionTypeID(Long transactionTypeID) {
		this.transactionTypeID = transactionTypeID;
	}

	public Long getChannelCodeID() {
		return channelCodeID;
	}

	public void setChannelCodeID(Long channelCodeID) {
		this.channelCodeID = channelCodeID;
	}
}
