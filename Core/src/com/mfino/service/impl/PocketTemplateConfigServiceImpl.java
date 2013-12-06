package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketTemplateConfigDAO;
import com.mfino.dao.query.PocketTemplateConfigQuery;
import com.mfino.domain.PocketTemplateConfig;
import com.mfino.service.PocketTemplateConfigService;

@Service("PocketTemplateConfigServiceImpl")
public class PocketTemplateConfigServiceImpl implements
		PocketTemplateConfigService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<PocketTemplateConfig> get(PocketTemplateConfigQuery query){
		PocketTemplateConfigDAO ptcDao = DAOFactory.getInstance().getPocketTemplateConfigDao();
		return ptcDao.get(query);
	}

}
