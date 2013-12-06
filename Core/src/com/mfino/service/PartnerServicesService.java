package com.mfino.service;

import java.util.List;

import com.mfino.domain.PartnerServices;

public interface PartnerServicesService {

	public PartnerServices getPartnerServices(long partnerId, long serviceProviderId, long serviceId);
	
	public List<PartnerServices> getPartnerServicesList(long partnerId, long serviceProviderId, long serviceId);
	
	public void save(PartnerServices ps);
	
	public PartnerServices getById(Long partnerServiceId);
}


