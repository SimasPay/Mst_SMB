package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.AutoReversalsDao;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.AutoReversals;
import com.mfino.service.AutoReversalsCoreService;

@Service("AutoReversalsCoreServiceImpl")
public class AutoReversalsCoreServiceImpl implements AutoReversalsCoreService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public AutoReversals getBySctlId(Long sctlId){
		AutoReversalsDao arDao = DAOFactory.getInstance().getAutoReversalsDao();
		return arDao.getBySctlId(sctlId);
	}
}
