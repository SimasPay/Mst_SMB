package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.service.MfinoServiceProviderService;

@Service("MfinoServiceProviderServiceImpl")
public class MfinoServiceProviderServiceImpl implements MfinoServiceProviderService{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public mFinoServiceProvider getMFSPbyID(int id){
	
		MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
		return mspDao.getById(id);
	}
}
