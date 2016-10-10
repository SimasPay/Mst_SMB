package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.domain.KycLevel;
import com.mfino.service.KYCLevelService;

@Service("KYCLevelServiceImpl")
public class KYCLevelServiceImpl implements KYCLevelService{
	/**
	 * Get KYCLevel record by the kyclevel
	 * @param kyclevel
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public KycLevel getByKycLevel(Long kyclevel) {
		KYCLevelDAO kycDAO = DAOFactory.getInstance().getKycLevelDAO();
		return kycDAO.getByKycLevel(kyclevel);
	}
}
