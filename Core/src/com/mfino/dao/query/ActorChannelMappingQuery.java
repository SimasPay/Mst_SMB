package com.mfino.dao.query;

/**
 * @author Srikanth
 * 
 */
public class ActorChannelMappingQuery extends BaseQuery {
	private Integer subscriberType;
	private Integer partnerType;
	private Long serviceID;
	private Long transactionTypeID;
	private Long channelCodeID;
	private Long kycLevel;
	private Long group;
	private Boolean isAllowed;

	public Integer getSubscriberType() {
		return subscriberType;
	}

	public void setSubscriberType(Integer subscriberType) {
		this.subscriberType = subscriberType;
	}

	public Integer getPartnerType() {
		return partnerType;
	}

	public void setPartnerType(Integer partnerType) {
		this.partnerType = partnerType;
	}

	public Long getServiceID() {
		return serviceID;
	}

	public void setServiceID(Long serviceID) {
		this.serviceID = serviceID;
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

	public Long getKycLevel() {
		return kycLevel;
	}

	public void setKycLevel(Long kycLevel) {
		this.kycLevel = kycLevel;
	}

	public Long getGroup() {
		return group;
	}

	public void setGroup(Long group) {
		this.group = group;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
}
