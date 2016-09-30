package com.mfino.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.DCTRestrictionsDao;
import com.mfino.dao.query.DCTRestrictionsQuery;
import com.mfino.domain.DCTRestrictions;
import com.mfino.domain.DistributionChainLevel;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceTransaction;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.DCTRestrictionsService;

/**
 * 
 * @author Sasi
 *
 */
@org.springframework.stereotype.Service("DCTRestrictionsServiceImpl")
public class DCTRestrictionsServiceImpl implements DCTRestrictionsService{
	
	private static Logger log = LoggerFactory.getLogger(DCTRestrictionsServiceImpl.class);
	
	/**
	 * Creates default restrictions for a dct
	 * @param dctLevel
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<DCTRestrictions> createDefautlRestrictions(DistributionChainLevel dctLevel) {
		List<DCTRestrictions> dctRestrictions = new ArrayList<DCTRestrictions>();
		log.info("DCTRestrictionsService :: createDefaultRestrictions() dct BEGIN");
		
		DistributionChainTemplate dct = dctLevel.getDistributionChainTemp();
		
		DCTRestrictionsDao dctRestrictionsDao = DAOFactory.getInstance().getDctRestrictionsDao();
		
		Service service = dct.getService();
		for(ServiceTransaction serviceTxn: service.getServiceTransactions()){
			TransactionType txnType = serviceTxn.getTransactionType();
			
			DCTRestrictions dctRestriction = new DCTRestrictions();
			dctRestriction.setDistributionChainTemplateByDCTID(dct);
			dctRestriction.setTransactionType(txnType);
			dctRestriction.setRelationshiptype(CmFinoFIX.RelationShipType_BELONGS_TO_TREE.longValue());
			Long temp = dctLevel.getDistributionlevel();
			dctRestriction.setDistributionlevel(temp);
			dctRestriction.setIsallowed((short) Boolean.compare(true, false));
			
			dctRestrictions.add(dctRestriction);
		}
		
		dctRestrictionsDao.save(dctRestrictions);
		
		log.info("DCTRestrictionsService :: createDefaultRestrictions() dct END");
		return dctRestrictions;
	}

	/**
	 * Deletes restrictions for the removed dctLevels
	 * @param dctLevels
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteRestrictions(List<DistributionChainLevel> dctLevels) {
		log.info("DCTRestrictionsService :: deleteRestrictions() dct BEGIN");
		
		DCTRestrictionsDao dctRestrictionsDao = DAOFactory.getInstance().getDctRestrictionsDao();
		
		for(DistributionChainLevel dctLevel : dctLevels){
			DCTRestrictionsQuery query = new DCTRestrictionsQuery();
			query.setDctId(dctLevel.getDistributionChainTemp().getId().longValue());
			query.setLevel((int) dctLevel.getDistributionlevel());
			
			List<DCTRestrictions> dctRestrictions = dctRestrictionsDao.get(query);
			if((null != dctRestrictions) && (dctRestrictions.size() > 0)){
				dctRestrictionsDao.delete(dctRestrictions);
			}
		}
		
		log.info("DCTRestrictionsService :: deleteRestrictions() dct END");
	}
	
	/**
	 * Gets the list of dct records based on the query
	 * @param query
	 * @return
	 */
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public List<DCTRestrictions> getDctRestrictions(DCTRestrictionsQuery query){
		List<DCTRestrictions> dctRestrictions = new ArrayList<DCTRestrictions>();
		DCTRestrictionsDao dctRestrictionsDao = DAOFactory.getInstance().getDctRestrictionsDao();
		dctRestrictions = dctRestrictionsDao.get(query);
		return dctRestrictions;
	}
}
