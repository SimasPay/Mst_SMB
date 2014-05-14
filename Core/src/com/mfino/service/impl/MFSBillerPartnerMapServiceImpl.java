package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSBillerPartnerDAO;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.service.MFSBillerPartnerMapService;

@Service("MFSBillerPartnerMapServiceImpl")
public class MFSBillerPartnerMapServiceImpl implements MFSBillerPartnerMapService{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MFSBillerPartner getByBillerCode(String billerCode) {
		MFSBillerPartnerDAO mfsbDAO = DAOFactory.getInstance().getMFSBillerPartnerDAO();
		return mfsbDAO.getByBillerCode(billerCode);
	}

}
