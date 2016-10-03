package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.UnregisteredTxnInfo;

/**
 * @author Sreenath
 *
 */
public interface UnRegisteredTxnInfoService {

	/**
	 * returns details of unregistered transaction using query which might contains mdn,fac and other related details
	 * @param query
	 * @return
	 */
	public List<UnregisteredTxnInfo> getUnRegisteredTxnInfoListByQuery(UnRegisteredTxnInfoQuery query);

	/**
	 * saves the details using UnRegisteredTxnInfoDAO
	 * @param unTxnInfo
	 */
	public void save(UnregisteredTxnInfo unTxnInfo);
	
	/**
	 * 
	 * @return
	 */
	public String generateFundAccessCode();

	

}
