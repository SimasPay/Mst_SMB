package com.mfino.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.DCTRestrictionsDao;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.query.DCTRestrictionsQuery;
import com.mfino.domain.DctRestrictions;
import com.mfino.domain.DistributionChainLvl;
import com.mfino.domain.DistributionChainTemp;
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
	public List<DctRestrictions> createDefautlRestrictions(DistributionChainLvl dctLevel) {
		List<DctRestrictions> dctRestrictions = new ArrayList<DctRestrictions>();
		log.info("DCTRestrictionsService :: createDefaultRestrictions() dct BEGIN");
		
		DistributionChainTemp dct = dctLevel.getDistributionChainTemp();
		
		DCTRestrictionsDao dctRestrictionsDao = DAOFactory.getInstance().getDctRestrictionsDao();
		
		ServiceDAO serviceDao = DAOFactory.getInstance().getServiceDAO();
		Service service = serviceDao.getById(dct.getServiceid());
		for(ServiceTransaction serviceTxn: service.getServiceTransactions()){
			TransactionType txnType = serviceTxn.getTransactionType();
			
			DctRestrictions dctRestriction = new DctRestrictions();
			dctRestriction.setDctid(dct.getId().longValue());
			dctRestriction.setTransactiontypeid(txnType.getId().longValue());
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
	public void deleteRestrictions(List<DistributionChainLvl> dctLevels) {
		log.info("DCTRestrictionsService :: deleteRestrictions() dct BEGIN");
		
		DCTRestrictionsDao dctRestrictionsDao = DAOFactory.getInstance().getDctRestrictionsDao();
		
		for(DistributionChainLvl dctLevel : dctLevels){
			DCTRestrictionsQuery query = new DCTRestrictionsQuery();
			query.setDctId(dctLevel.getDistributionChainTemp().getId().longValue());
			query.setLevel((int) dctLevel.getDistributionlevel());
			
			List<DctRestrictions> dctRestrictions = dctRestrictionsDao.get(query);
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
	public List<DctRestrictions> getDctRestrictions(DCTRestrictionsQuery query){
		List<DctRestrictions> dctRestrictions = new ArrayList<DctRestrictions>();
		DCTRestrictionsDao dctRestrictionsDao = DAOFactory.getInstance().getDctRestrictionsDao();
		dctRestrictions = dctRestrictionsDao.get(query);
		return dctRestrictions;
	}
}
