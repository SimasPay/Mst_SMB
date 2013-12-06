package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.ServiceSettlementConfigQuery;
import com.mfino.domain.ServiceSettlementConfig;

public interface ServiceSettlementConfigCoreService {
	public List<ServiceSettlementConfig> get(ServiceSettlementConfigQuery query);
	public void save(ServiceSettlementConfig sc);
}
