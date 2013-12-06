/**
 * 
 */
package com.mfino.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MoneyClearanceGravedDAO;
import com.mfino.dao.query.MoneyClearanceGravedQuery;
import com.mfino.domain.MoneyClearanceGraved;
import com.mfino.service.MoneyClearanceGravedService;

/**
 * Service class for all MoneyClearanceGraved related database access
 * @author Sreenath
 *
 */
@Service("MoneyClearanceGravedServiceImpl")
public class MoneyClearanceGravedServiceImpl implements MoneyClearanceGravedService{

	private Logger log = LoggerFactory.getLogger(MoneyClearanceGravedServiceImpl.class);

	/**
	 * Returns the list of records from MoneyClearanceGraved table satisfying the query inputs
	 * @param mcgQuery
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<MoneyClearanceGraved> getMoneyClearanceGravedByQuery(MoneyClearanceGravedQuery mcgQuery) {
		log.info("getting the MoneyClearanceGraved records for the query: "+mcgQuery);
		MoneyClearanceGravedDAO moneyClearanceGravedDAO = DAOFactory.getInstance().getMoneyClearanceGravedDao();
		List<MoneyClearanceGraved> lstMoneyClearanceGraveds = moneyClearanceGravedDAO.get(mcgQuery);
		return lstMoneyClearanceGraveds;
	}

	/**
	 * Saves the moneyClearanceGraved record into the database
	 * @param moneyClearanceGraved
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void saveMoneyClearanceGraved(MoneyClearanceGraved moneyClearanceGraved) {
		log.info("Saving the MoneyClearanceGraved record "+moneyClearanceGraved);
		MoneyClearanceGravedDAO moneyClearanceGravedDAO = DAOFactory.getInstance().getMoneyClearanceGravedDao();
		moneyClearanceGravedDAO.save(moneyClearanceGraved);
	}

}
