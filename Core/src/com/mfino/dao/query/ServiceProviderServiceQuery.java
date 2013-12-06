package com.mfino.dao.query;


/**
 * @author Bala Sunku
 *
 */
public class ServiceProviderServiceQuery extends BaseQuery {

	private Long serviceId;
	private Long serviceProviderId;
	
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public Long getServiceProviderId() {
		return serviceProviderId;
	}
	public void setServiceProviderId(Long serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}


}
