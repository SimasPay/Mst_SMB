package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.FundDistributionInfoQuery;
import com.mfino.domain.FundDefinition;
import com.mfino.domain.FundDistributionInfo;
import com.mfino.domain.UnregisteredTxnInfo;

public interface FundStorageService {
	/**
	 * Saves the Unregistered trxn info record to database
	 * @param unRegTxnInfo
	 */
	void allocateFunds(UnregisteredTxnInfo unRegTxnInfo);
	/**
	 * digests the code with the subscriberMDN to store in database
	 * @param subscriberMDN
	 * @param code
	 * @return
	 */
	String generateDigestedFAC(String subscriberMDN, String code);
	/**
	 * 
	 * @param fundDistributionInfo
	 */
	void withdrawFunds(FundDistributionInfo fundDistributionInfo);
	/**
	 * Gets the list of fundDistribution records matching the query
	 * @param fundDistributionInfoQuery
	 * @return
	 */
	public List<FundDistributionInfo> getFundDistributionInfosByQuery(
			FundDistributionInfoQuery fundDistributionInfoQuery);
	
	/**
	 * Generates new fac by taking required data from the FundDefinition record
	 * @param fundDef
	 * @return
	 */
	public String generateFundAccessCode(FundDefinition fundDef);

}
