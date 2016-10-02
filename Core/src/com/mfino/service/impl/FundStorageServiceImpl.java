package com.mfino.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.FundDistributionInfoDAO;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.FundDistributionInfoQuery;
import com.mfino.domain.FundDefinition;
import com.mfino.domain.FundDistributionInfo;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.service.FundStorageService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.MfinoUtil;

@Service("FundStorageServiceImpl")
public class FundStorageServiceImpl implements FundStorageService {
	
	private static Logger log = LoggerFactory.getLogger(FundStorageServiceImpl.class);	
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public void allocateFunds(UnregisteredTxnInfo unRegTxnInfo) {
		UnRegisteredTxnInfoDAO unregTrxnInfoDAO = DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
		unregTrxnInfoDAO.save(unRegTxnInfo);		
	}

	public String generateDigestedFAC(String subscriberMDN, String code) {
		return MfinoUtil.calculateDigestPin(subscriberMDN, code);
	}	

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public void withdrawFunds(FundDistributionInfo fundDistributionInfo) {
		FundDistributionInfoDAO fundDistributionInfoDAO = DAOFactory.getInstance().getFundDistributionInfoDAO();
		fundDistributionInfoDAO.save(fundDistributionInfo);	
	}

	/**
	 * Gets the list of fundDistribution records matching the query
	 * @param fundDistributionInfoQuery
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<FundDistributionInfo> getFundDistributionInfosByQuery(
			FundDistributionInfoQuery fundDistributionInfoQuery) {
		FundDistributionInfoDAO fundDistributionInfoDAO = DAOFactory.getInstance().getFundDistributionInfoDAO();
		List<FundDistributionInfo> lstFundDistributionInfos = fundDistributionInfoDAO.get(fundDistributionInfoQuery);
		return lstFundDistributionInfos;
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public String generateFundAccessCode(FundDefinition fundDef){
		String prefix = fundDef.getFacprefix();
		prefix = (prefix == null) ? StringUtils.EMPTY : prefix; 
		String random = generateOTP(fundDef);
		
		return prefix + random;
	}
	
	private String generateOTP(FundDefinition fundDef) {
		int prefixLen = (fundDef.getFacprefix() == null) ? 0 : fundDef.getFacprefix().length();
		Integer length = (int) (fundDef.getFaclength()-prefixLen);
		if(length==null || length < 4){
			log.info("fac length in fund Definition is null or is less than 4.Using default length from the system parameter: otp.length");
			length = systemParametersService.getOTPLength();;
		}
		return MfinoUtil.generateRandomNumber(length);
	}

    
}
