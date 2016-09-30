package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.ServiceSettlementConfigQuery;
import com.mfino.domain.ServiceSettlementCfg;

public interface ServiceSettlementConfigCoreService {
	public List<ServiceSettlementCfg> get(ServiceSettlementConfigQuery query);
	public void save(ServiceSettlementCfg sc);
}
