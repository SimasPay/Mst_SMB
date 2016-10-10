/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.DCTRestrictionsQuery;
import com.mfino.domain.DCTRestrictions;
import com.mfino.domain.DistributionChainLvl;

/**
 * @author Sreenath
 *
 */
public interface DCTRestrictionsService {	
	/**
	 * Creates default restrictions for a dct
	 * @param dctLevel
	 * @return
	 */
	public List<DCTRestrictions> createDefautlRestrictions(DistributionChainLvl dctLevel);

	/**
	 * Deletes restrictions for the removed dctLevels
	 * @param dctLevels
	 */
	public void deleteRestrictions(List<DistributionChainLvl> dctLevels);
	
	/**
	 * Gets the list of dct records based on the query
	 * @param query
	 * @return
	 */
	public List<DCTRestrictions> getDctRestrictions(DCTRestrictionsQuery query);


}
