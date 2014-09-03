package com.mfino.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CashinFirstTimeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.CashinFirstTime;
import com.mfino.service.CashinFirstTimeService;

/**
 * 
 * @author Vishal
 */
@Service("CashinFirstTimeServiceImpl")
public class CashinFirstTimeServiceImpl implements CashinFirstTimeService{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CashinFirstTime getByMDN(String MDN) {
		if(StringUtils.isNotBlank(MDN)){
			CashinFirstTimeDAO cftDAO = DAOFactory.getInstance().getCashinFirstTimeDAO();
			return cftDAO.getByMDN(MDN);
		}
		else{
			return null;
		}
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void saveCashinFirstTime(CashinFirstTime cft) {
		CashinFirstTimeDAO cftDAO = DAOFactory.getInstance().getCashinFirstTimeDAO();
		cftDAO.save(cft);
	}

}
