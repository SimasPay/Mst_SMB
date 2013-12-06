package com.mfino.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.domain.PartnerServices;
import com.mfino.service.PartnerService;
import com.mfino.service.PartnerServicesService;

@Service("PartnerServicesServiceImpl")
public class PartnerServicesServiceImpl implements PartnerServicesService{
	private static Logger log = LoggerFactory.getLogger(PartnerServicesServiceImpl.class);

	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public PartnerServices getPartnerServices(long partnerId, long serviceProviderId, long serviceId) {
		PartnerServices ps = null;
		List<PartnerServices> lstPS =getPartnerServicesList(partnerId, serviceProviderId, serviceId);
		if (CollectionUtils.isNotEmpty(lstPS)) {
			ps = lstPS.get(0);
		}
		return ps;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<PartnerServices> getPartnerServicesList(long partnerId, long serviceProviderId, long serviceId){
		PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();
		List<PartnerServices> lstPS = psDAO.getPartnerServices(partnerId, serviceProviderId, serviceId);
		if(CollectionUtils.isEmpty(lstPS)){
			log.error("PartnerServices list obtained null");
		}
		return lstPS;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(PartnerServices ps){
		PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();
		psDAO.save(ps);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public PartnerServices getById(Long partnerServiceId){
		PartnerServicesDAO psDAO = DAOFactory.getInstance().getPartnerServicesDAO();
		return psDAO.getById(partnerServiceId);
	}
}
