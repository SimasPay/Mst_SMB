package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BranchCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BranchCodes;
import com.mfino.service.BranchCodeService;

@Service("BranchCodeServiceImpl")
public class BranchCodeServiceImpl implements BranchCodeService {

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BranchCodes getById(Long id) {
		BranchCodeDAO branchCodeDAO = DAOFactory.getInstance().getBranchCodeDAO();
		return branchCodeDAO.getById(id);
	}

}
