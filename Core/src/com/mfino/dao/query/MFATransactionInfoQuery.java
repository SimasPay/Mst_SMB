package com.mfino.dao.query;
/**
 * @author Satya
 *
 */
public class MFATransactionInfoQuery extends BaseQuery{
	private Long serviceId, transactionTypeId, channelCodeId;
	private Integer mfaMode;
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
	public Integer getMfaMode() {
		return mfaMode;
	}
	public void setMfaMode(Integer mfaMode) {
		this.mfaMode = mfaMode;
	}
	
}
