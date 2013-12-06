package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ScheduleTemplateDAO;
import com.mfino.domain.ScheduleTemplate;
import com.mfino.service.ScheduleTemplateService;

@Service("ScheduleTemplateServiceImpl")
public class ScheduleTemplateServiceImpl implements ScheduleTemplateService {
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public ScheduleTemplate getScheduleTemplateById(Long stId){
	ScheduleTemplateDAO stDao = DAOFactory.getInstance().getScheduleTemplateDao();
	return stDao.getById(stId);
	}

}
