package com.mfino.dao.query;

public class PartnerServicesQuery extends BaseQuery{
	
	private Long partnerId;
	private Long serviceId;
	
	public Long getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(Long partnerId) {
		this.partnerId = partnerId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
}
