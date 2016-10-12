/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.DAOConstants;
import com.mfino.domain.MfsLedger;
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
			List<MfsLedger> lstMfsLedgers = mFSLedgerService.getLedgerEntriesByLedgerStatus(DAOConstants.LEDGER_STATUS_DEFERED);
			
			if (CollectionUtils.isNotEmpty(lstMfsLedgers)) {
				for (MfsLedger mfsLedger : lstMfsLedgers) {
					try {
						
						updatePocketBalance(mfsLedger);
						
					} catch (Exception e) {
						log.error("Error while processing the ledger entry with SCTL id = " + mfsLedger.getSctlid() + " and pocket id = " + mfsLedger.getPocketid(), e);
					}
				}
			}
			
		} catch (Exception e) {
			log.error("Error: While Processing ledger entries " + e.getMessage(), e);
		} 
		log.info("updatePocketBalancesFromLedger :: END");
	}
  private void updatePocketBalance(MfsLedger mfsLedger){
	MoneyService ms = CoreServiceFactory.getInstance().getMoneyService();

	Pocket pocket = null;
	log.info("Updating the pocket balance for the SCTL id = " + mfsLedger.getSctlid() + " and pocket id = " 
			+ mfsLedger.getPocketid() + " with amount = " + mfsLedger.getAmount());
	pocket = pocketService.getById(mfsLedger.getPocketid().longValue(), LockMode.UPGRADE);
	log.debug("Pocket balance before update = " + pocket.getCurrentbalance());
	
	if (DAOConstants.DEBIT_LEDGER_TYPE.equals(mfsLedger.getLedgertype())) {
		pocket.setCurrentbalance(ms.subtract(new BigDecimal(pocket.getCurrentbalance()), mfsLedger.getAmount()).toPlainString());
	}
	else {
		pocket.setCurrentbalance(ms.add(new BigDecimal(pocket.getCurrentbalance()), mfsLedger.getAmount()).toPlainString());
	}
	
	mfsLedger.setLedgerstatus(DAOConstants.LEDGER_STATUS_UPDATED);
	pocketService.save(pocket);
	mFSLedgerService.save(mfsLedger);
	log.debug("Pocket balance after update = " + pocket.getCurrentbalance());						
	log.info("Updated the pocket balance for the SCTL id = " + mfsLedger.getSctlid() + " and pocket id = " + mfsLedger.getPocketid());
	
}
}
