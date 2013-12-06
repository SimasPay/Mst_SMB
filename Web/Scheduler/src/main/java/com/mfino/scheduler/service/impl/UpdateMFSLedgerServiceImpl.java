/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.DAOConstants;
import com.mfino.domain.MFSLedger;
import com.mfino.domain.Pocket;
import com.mfino.scheduler.service.UpdateMFSLedgerService;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.MFSLedgerService;
import com.mfino.service.MoneyService;
import com.mfino.service.PocketService;

/**
 * @author Bala Sunku
 *
 */
@Service("UpdateMFSLedgerServiceImpl")
public class UpdateMFSLedgerServiceImpl  implements UpdateMFSLedgerService {
	private static Logger log = LoggerFactory.getLogger(UpdateMFSLedgerServiceImpl.class);
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("MFSLedgerServiceImpl")
	private MFSLedgerService mFSLedgerService;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	public void updatePocketBalancesFromLedger() {
		log.info("updatePocketBalancesFromLedger :: BEGIN");
		try {
			List<MFSLedger> lstMfsLedgers = mFSLedgerService.getLedgerEntriesByLedgerStatus(DAOConstants.LEDGER_STATUS_DEFERED);
			
			if (CollectionUtils.isNotEmpty(lstMfsLedgers)) {
				for (MFSLedger mfsLedger : lstMfsLedgers) {
					try {
						
						updatePocketBalance(mfsLedger);
						
					} catch (Exception e) {
						log.error("Error while processing the ledger entry with SCTL id = " + mfsLedger.getSctlId() + " and pocket id = " + mfsLedger.getPocketID(), e);
					}
				}
			}
			
		} catch (Exception e) {
			log.error("Error: While Processing ledger entries " + e.getMessage(), e);
		} 
		log.info("updatePocketBalancesFromLedger :: END");
	}
  private void updatePocketBalance(MFSLedger mfsLedger){
	MoneyService ms = CoreServiceFactory.getInstance().getMoneyService();

	Pocket pocket = null;
	log.info("Updating the pocket balance for the SCTL id = " + mfsLedger.getSctlId() + " and pocket id = " 
			+ mfsLedger.getPocketID() + " with amount = " + mfsLedger.getAmount());
	pocket = pocketService.getById(mfsLedger.getPocketID(), LockMode.UPGRADE);
	log.debug("Pocket balance before update = " + pocket.getCurrentBalance());
	
	if (DAOConstants.DEBIT_LEDGER_TYPE.equals(mfsLedger.getLedgerType())) {
		pocket.setCurrentBalance(ms.subtract(pocket.getCurrentBalance(), mfsLedger.getAmount()));
	}
	else {
		pocket.setCurrentBalance(ms.add(pocket.getCurrentBalance(), mfsLedger.getAmount()));
	}
	
	mfsLedger.setLedgerStatus(DAOConstants.LEDGER_STATUS_UPDATED);
	pocketService.save(pocket);
	mFSLedgerService.save(mfsLedger);
	log.debug("Pocket balance after update = " + pocket.getCurrentBalance());						
	log.info("Updated the pocket balance for the SCTL id = " + mfsLedger.getSctlId() + " and pocket id = " + mfsLedger.getPocketID());
	
}
}
