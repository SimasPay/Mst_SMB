package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceSettlementConfigDAO;
import com.mfino.dao.query.ServiceSettlementConfigQuery;
import com.mfino.domain.ServiceSettlementCfg;
import com.mfino.service.ServiceSettlementConfigCoreService;

@Service("ServiceSettlementConfigCoreServiceImpl")
public class ServiceSettlementConfigCoreServiceImpl implements
		ServiceSettlementConfigCoreService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<ServiceSettlementCfg> get(ServiceSettlementConfigQuery query){
		ServiceSettlementConfigDAO sscDao = DAOFactory.getInstance().getServiceSettlementConfigDAO();
        return sscDao.get(query);  
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(ServiceSettlementCfg sc){
		ServiceSettlementConfigDAO sscDao = DAOFactory.getInstance().getServiceSettlementConfigDAO();
		sscDao.save(sc);
	}
}
