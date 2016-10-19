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
			if (bool_true.equals(pocket.getIsdefault())) {
				for (Pocket simillarPocket : simillarPockets) {
					if (!simillarPocket.getId().equals( pocket.getId()))
						if (bool_true.equals(simillarPocket.getIsdefault())) {
							simillarPocket.setIsdefault(true);
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
					if (! simillarPocket.getId().equals(pocket.getId()))
						if (bool_true.equals(simillarPocket.getIsdefault())) {
							thereIsAlreadyAnotherDefault++;
							if (thereIsAlreadyAnotherDefault > 1) {
								// if there are more than one that is default,
								// log error and correct the situation
								log.error("More than one default pocket for type: " + pocket.getPocketTemplateByPockettemplateid().getType()
										+ " and commodity: " + pocket.getPocketTemplateByPockettemplateid().getCommodity());
								simillarPocket.setIsdefault(true);
								pocketDAO.save(simillarPocket);
							}
						}
				}

				if (isNew) {
					pocket.setIsdefault(true);
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
		query.setMdnIDSearch(p.getSubscriberMdn().getId().longValue());
		
		Long pocketTypeL = p.getPocketTemplateByPockettemplateid().getType().longValue();
		Integer pocketTypeLI = pocketTypeL.intValue();
		query.setPocketType(pocketTypeLI);
		query.setIsCollectorPocket(Boolean.valueOf(p.getPocketTemplateByPockettemplateid().getIscollectorpocket().toString()));
		query.setIsSuspencePocketAllowed(Boolean.valueOf(p.getPocketTemplateByPockettemplateid().getIssuspencepocket().toString()));
		return pocketDAO.get(query);
	}
}
