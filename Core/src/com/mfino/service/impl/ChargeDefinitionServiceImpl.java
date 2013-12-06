package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeDefinitionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.ChargeDefinitionQuery;
import com.mfino.domain.ChargeDefinition;
import com.mfino.service.ChargeDefinitionService;

@Service("ChargeDefinitionServiceImpl")
public class ChargeDefinitionServiceImpl implements ChargeDefinitionService {

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<ChargeDefinition> get(ChargeDefinitionQuery query) {
		ChargeDefinitionDAO chargeDefDao =DAOFactory.getInstance().getChargeDefinitionDAO();
        return chargeDefDao.get(query);
	}

}
