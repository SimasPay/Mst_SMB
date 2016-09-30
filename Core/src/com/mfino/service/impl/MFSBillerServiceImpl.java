package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSBillerDAO;
import com.mfino.domain.MfsBiller;
import com.mfino.service.MFSBillerService;

@Service("MFSBillerServiceImpl")
public class MFSBillerServiceImpl implements MFSBillerService{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MfsBiller getByBillerCode(String billerCode) {
		MFSBillerDAO mfsbDAO = DAOFactory.getInstance().getMFSBillerDAO();
		return mfsbDAO.getByBillerCode(billerCode);
	}

}
