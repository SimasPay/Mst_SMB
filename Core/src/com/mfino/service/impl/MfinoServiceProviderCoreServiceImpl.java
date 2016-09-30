package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.service.MfinoServiceProviderCoreService;

@Service("MfinoServiceProviderCoreServiceImpl")
public class MfinoServiceProviderCoreServiceImpl implements
		MfinoServiceProviderCoreService {
	 @Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	    public mFinoServiceProvider getById(long id) {
			MfinoServiceProviderDAO dao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
	    	return dao.getById(id);
	    }
}
