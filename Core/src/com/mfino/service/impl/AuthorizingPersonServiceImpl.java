package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.AuthorizingPersonDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.AuthPersonDetails;
import com.mfino.service.AuthorizingPersonService;

@Service("AuthorizingPersonServiceImpl")
public class AuthorizingPersonServiceImpl implements AuthorizingPersonService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(AuthPersonDetails s){
		AuthorizingPersonDAO authorizingPersonDAO = DAOFactory.getInstance().getAuthorizingPersonDAO();
		authorizingPersonDAO.save(s);
	}

}
