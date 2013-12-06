package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscribersAdditionalFieldsDAO;
import com.mfino.domain.SubscribersAdditionalFields;
import com.mfino.service.SubscribersAdditionalFieldsService;

@Service("SubscribersAdditionalFieldsServiceImpl")
public class SubscribersAdditionalFieldsServiceImpl implements
		SubscribersAdditionalFieldsService {

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(
			SubscribersAdditionalFields subscribersAdditionalFields) {
		SubscribersAdditionalFieldsDAO subscribersAdditionalFieldsDAO = DAOFactory.getInstance().getSubscribersAdditionalFieldsDAO();
		subscribersAdditionalFieldsDAO.save(subscribersAdditionalFields);
	}

}
