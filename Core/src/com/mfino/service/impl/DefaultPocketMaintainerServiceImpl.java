/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.Pocket;
import com.mfino.errorcodes.Codes;
import com.mfino.service.DefaultPocketMaintainerService;

/**
 * 
 * @author sandeepjs
 */
@Service("DefaultPocketMaintainerServiceImpl")
public class DefaultPocketMaintainerServiceImpl implements  DefaultPocketMaintainerService{

	private PocketDAO	   pocketDAO	= DAOFactory.getInstance().getPocketDAO();
	private static Logger log = LoggerFactory.getLogger(DefaultPocketMaintainerServiceImpl.class);

	/**
	 * Checks and changes the isDefault status of the given pocket
	 * @param pocket
	 * @param isNew
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int setDefaultPocket(Pocket pocket, boolean isNew) {

		log.info("Start Processing pocket");

		/*
		 * get all pockets with same pocket type as that of a given pocket for a particular subscriber(or partner)
		 */
		List<Pocket> simillarPockets = getSimillarTypePockets(pocket);
		// if the current pocket is default, set all other pockets to non
		// default
		try {
			Boolean bool_true =Boolean.valueOf(true);
			if (bool_true.equals(pocket.getIsDefault())) {
				for (Pocket simillarPocket : simillarPockets) {
					if (!simillarPocket.getID().equals( pocket.getID()))
						if (bool_true.equals(simillarPocket.getIsDefault())) {
							simillarPocket.setIsDefault(false);
							pocketDAO.save(simillarPocket);
						}
				}
				return Codes.SUCCESS;
			}
			else {
				// if the current pocket is non default, check if there is
				// another one that is default
				int thereIsAlreadyAnotherDefault = 0;
				for (Pocket simillarPocket : simillarPockets) {
					if (! simillarPocket.getID().equals(pocket.getID()))
						if (bool_true.equals(simillarPocket.getIsDefault())) {
							thereIsAlreadyAnotherDefault++;
							if (thereIsAlreadyAnotherDefault > 1) {
								// if there are more than one that is default,
								// log error and correct the situation
								log.error("More than one default pocket for type: " + pocket.getPocketTemplate().getType()
										+ " and commodity: " + pocket.getPocketTemplate().getCommodity());
								simillarPocket.setIsDefault(false);
								pocketDAO.save(simillarPocket);
							}
						}
				}

				if (isNew) {
					pocket.setIsDefault(true);
				}
				return Codes.SUCCESS;
			}
		}
		catch (Exception ex) {
			log.error("Error setting default pocket: ",ex);
			return Codes.FAILURE;
		}
	}

	/**
	 * 
	 * @param pocket
	 * @return
	 */
	private List<Pocket> getSimillarTypePockets(Pocket p) {
		PocketQuery query = new PocketQuery();
		query.setMdnIDSearch(p.getSubscriberMDNByMDNID().getID());
		query.setPocketType(p.getPocketTemplate().getType());
		query.setIsCollectorPocket(p.getPocketTemplate().getIsCollectorPocket());
		query.setIsSuspencePocketAllowed(p.getPocketTemplate().getIsSuspencePocket());
		return pocketDAO.get(query);
	}
}
