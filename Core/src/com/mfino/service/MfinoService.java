package com.mfino.service;

import com.mfino.domain.Service;

public interface MfinoService {

	Service getByServiceID(Long serviceID);
	public Service getServiceByName(String serviceName);

}
