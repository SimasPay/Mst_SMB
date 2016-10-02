/**
 * 
 */
package com.mfino.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.service.SystemParametersService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.util.MfinoUtil;

/**
 * @author Sreenath
 *
 */
@Service("UnRegisteredTxnInfoServiceImpl")
public class UnRegisteredTxnInfoServiceImpl implements UnRegisteredTxnInfoService{
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	/**
	 * returns details of unregistered transaction using query which might contains mdn,fac and other related details
	 * @param query
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<UnRegisteredTxnInfo> getUnRegisteredTxnInfoListByQuery(UnRegisteredTxnInfoQuery query) {
		UnRegisteredTxnInfoDAO unRegisteredTxnInfoDAO = DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
		List<UnRegisteredTxnInfo> listUnRegisteredTxnInfo = unRegisteredTxnInfoDAO.get(query);
		return listUnRegisteredTxnInfo;
		
	}
	/**
	 * saves the details using UnRegisteredTxnInfoDAO
	 * @param unTxnInfo
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void save(UnRegisteredTxnInfo unTxnInfo) {
		UnRegisteredTxnInfoDAO unRegisteredTxnInfoDAO = DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
		unRegisteredTxnInfoDAO.save(unTxnInfo);
		
	}
	
	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public String generateFundAccessCode() {
		String prefix = systemParametersService.getString(SystemParameterKeys.FAC_PREFIX_VALUE);
		prefix = (prefix == null) ? StringUtils.EMPTY : prefix; 
		Integer OTPLength = systemParametersService.getOTPLength();
		String random = MfinoUtil.generateOTP(OTPLength);		
		return prefix + random;
	}

	
}
