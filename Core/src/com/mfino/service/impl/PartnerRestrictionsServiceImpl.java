package com.mfino.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerRestrictionsDao;
import com.mfino.dao.query.PartnerRestrictionsQuery;
import com.mfino.domain.PartnerRestrictions;
import com.mfino.service.PartnerRestrictionsService;

/**
 * 
 * @author Sasi
 *
 */
@Service("PartnerRestrictionsServiceImpl")
public class PartnerRestrictionsServiceImpl implements PartnerRestrictionsService{
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<PartnerRestrictions> getPartnerRestrictions(PartnerRestrictionsQuery query){
		PartnerRestrictionsDao partnerRestrictionsDao = DAOFactory.getInstance().getPartnerRestrictionsDao();
		return partnerRestrictionsDao.get(query);
	}
	
}
