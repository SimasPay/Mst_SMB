package com.mfino.mce.backend.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.backend.CommodityTransferService;
import com.mfino.mce.backend.PendingCommodityTransfer21CtClearance;
import com.mfino.service.TransactionChargingService;

/**
 * @author srinivaas
 *
 */
public class PendingCommodityTransfer21CtClearanceImpl extends BaseServiceImpl implements PendingCommodityTransfer21CtClearance{

	private SessionFactory sessionFactory;

	private CommodityTransferService commodityTransferService;

	private TransactionChargingService transactionChargingService ;


	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<PendingCommodityTransfer> getAll21NonPendingTransfers(){
		return coreDataWrapper.getAll21NonPendingTransfers();
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW,rollbackFor=Throwable.class)
	public void calculateFinalState(PendingCommodityTransfer pct) {
		
		log.info("Moving PCT to CT: "+pct.getID());
		pct.setTransferStatus(CmFinoFIX.TransferStatus_Failed);
		pct.setTransferFailureReason(99);
		getCommodityTransferService().movePctToCt(pct);
		transactionChargingService.setAsFailed(pct.getID(), "Manually moved from PCT to CT table");
	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param commodityTransferService the commodityTransferService to set
	 */
	public void setCommodityTransferService(CommodityTransferService commodityTransferService) {
		this.commodityTransferService = commodityTransferService;
	}

	/**
	 * @return the commodityTransferService
	 */
	public CommodityTransferService getCommodityTransferService() {
		return commodityTransferService;
	}

	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public void setTransactionChargingService(
			TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}
}
