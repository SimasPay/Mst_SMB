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

import com.mfino.dao.ClosedAccountSettlementMDNDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.ClosedAccountSettlementMDNQuery;
import com.mfino.domain.CloseAcctSetlMdn;
import com.mfino.service.ClosedAccountSettlementMDNService;

/**
 * @author Sreenath
 *
 */
@Service("ClosedAccountSettlementMDNServiceImpl")
public class ClosedAccountSettlementMDNServiceImpl implements ClosedAccountSettlementMDNService{

	private Logger log = LoggerFactory.getLogger(ClosedAccountSettlementMDNServiceImpl.class);

	/**
	 * Returns the list of records from ClosedAccountSettlementMDN table satisfying the query inputs
	 * @param casmQuery
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<CloseAcctSetlMdn> getClosedAccountSettlementMDNByQuery(
			ClosedAccountSettlementMDNQuery casmQuery) {
		log.info("Getting the ClosedAccountSettlementMDN records for the query: "+casmQuery);
		ClosedAccountSettlementMDNDAO closedAccountSettlementMDNDAO = DAOFactory.getInstance().getClosedAccountSettlementMdnDao();
		List<CloseAcctSetlMdn> lstClosedAccountSettlementMDNs = closedAccountSettlementMDNDAO.get(casmQuery);
		return lstClosedAccountSettlementMDNs;
	}

	/**
	 * Saves the closedAccountSettlementMDN record into the database
	 * @param closedAccountSettlementMDN
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void saveClosedAccountSettlementMDN(CloseAcctSetlMdn closedAccountSettlementMDN) {
		log.info("Saving the closedAccountSettlementMDN record "+closedAccountSettlementMDN);
		ClosedAccountSettlementMDNDAO closedAccountSettlementMDNDAO = DAOFactory.getInstance().getClosedAccountSettlementMdnDao();
		closedAccountSettlementMDNDAO.save(closedAccountSettlementMDN);
	}

}
