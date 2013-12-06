package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceSettlementConfigDAO;
import com.mfino.dao.query.ServiceSettlementConfigQuery;
import com.mfino.domain.ServiceSettlementConfig;
import com.mfino.service.ServiceSettlementConfigCoreService;

@Service("ServiceSettlementConfigCoreServiceImpl")
public class ServiceSettlementConfigCoreServiceImpl implements
		ServiceSettlementConfigCoreService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<ServiceSettlementConfig> get(ServiceSettlementConfigQuery query){
		ServiceSettlementConfigDAO sscDao = DAOFactory.getInstance().getServiceSettlementConfigDAO();
        return sscDao.get(query);  
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(ServiceSettlementConfig sc){
		ServiceSettlementConfigDAO sscDao = DAOFactory.getInstance().getServiceSettlementConfigDAO();
		sscDao.save(sc);
	}
}
