package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.RetiredCardPANInfoDAO;
import com.mfino.dao.query.RetiredCardPANInfoQuery;
import com.mfino.domain.RetiredCardPANInfo;
import com.mfino.service.RetiredCardPANInfoService;

@Service("RetiredCardPANInfoServiceImpl")
public class RetiredCardPANInfoServiceImpl implements RetiredCardPANInfoService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<RetiredCardPANInfo> get(RetiredCardPANInfoQuery query){
		RetiredCardPANInfoDAO dao = DAOFactory.getInstance().getRetiredCardPANInfoDAO();
		return dao.get(query);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void  save(RetiredCardPANInfo retiredCardPANInfo){
		RetiredCardPANInfoDAO dao = DAOFactory.getInstance().getRetiredCardPANInfoDAO();
		 dao.save(retiredCardPANInfo);
	}
}
