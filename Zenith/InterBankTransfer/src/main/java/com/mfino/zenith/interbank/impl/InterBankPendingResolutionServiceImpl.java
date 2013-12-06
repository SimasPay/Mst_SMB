package com.mfino.zenith.interbank.impl;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.InterBankTransfersDao;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.domain.InterbankTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferStatus;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.impl.BaseServiceImpl;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.UniqueNumberGen;
import com.mfino.zenith.interbank.InteBankPendingResolutionService;

public class InterBankPendingResolutionServiceImpl extends BaseServiceImpl implements InteBankPendingResolutionService, Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		log.debug("InterbankPendingResolutionServiceImpl :: process() BEGIN");
		CMInterBankFundsTransferStatus fundsTransferStatus = movePendingToComplete();
		
		MCEMessage mceMessage = new MCEMessage();
		
		if(fundsTransferStatus != null){
			mceMessage.setRequest(fundsTransferStatus);
			exchange.getOut().setBody(mceMessage);
		}

		log.debug("InterbankPendingResolutionServiceImpl :: process() BEGIN");
	}
	
	public CMInterBankFundsTransferStatus movePendingToComplete() {
		log.info("PendingCommodityTransferClearanceImpl:: movePendingToComplete() Begin");

		PendingCommodityTransferDAO pctDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
		CommodityTransferQuery query = new CommodityTransferQuery();
		query.setUiCategory(CmFinoFIX.TransactionUICategory_InterBank_Transfer);
		
		try {
			List<PendingCommodityTransfer> lst = pctDao.get(query);
			
			for (PendingCommodityTransfer pct: lst) {
				try{
					return resolvePendingTransfer(pct);				
				}
				catch (Exception e) {
					log.error("Exception in movePendingToComplete ",e);
				}
			}
		} catch (Exception e1) {
			log.error("ERROR : PendingC");
		}
		
		
		log.info("PendingCommodityTransferClearanceImpl:: movePendingToComplete() End");
		return null;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	private CMInterBankFundsTransferStatus resolvePendingTransfer(PendingCommodityTransfer pct) {
		boolean isExpired = checkExpiredTransfer(pct);
		
		if(isExpired){
			ServiceChargeTransactionLog sctl = getSctlByTransactionLogId(pct.getTransactionsLogByTransactionID().getID());
			CMInterBankFundsTransferStatus txnStatus = new CMInterBankFundsTransferStatus();
			InterbankTransfer ibt = getIBT(sctl.getID());			
			txnStatus.setDestBankCode(ibt.getDestBankCode());
			txnStatus.setTerminalID(ibt.getTerminalID());
			txnStatus.setPaymentReference(ibt.getPaymentReference());
			txnStatus.setParentTransactionID(ibt.getTransferID());
			txnStatus.setServiceChargeTransactionLogID(ibt.getSctlId());
			
			txnStatus.header().setSendingTime(new Timestamp());
			txnStatus.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
			txnStatus.setSourceApplication(3);
			txnStatus.setSourceMDN(pct.getSourceMDN());
			
			return txnStatus;
		}
		
		return null;
	}

	private boolean checkExpiredTransfer(PendingCommodityTransfer pct) {
		Timestamp now = new Timestamp();
		
		if ((pct.getStartTime().getTime() + pct.getExpirationTimeout()) < now.getTime()
				&&(CmFinoFIX.TransferStatus_Pending.equals(pct.getTransferStatus()))) {
			return true;
		}
		
		return false;
	}

	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public InterbankTransfer getIBT(Long sctlId){
		
		if(sctlId == null) return null;
		
		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		InterBankTransfersQuery query = new InterBankTransfersQuery();
		query.setSctlId(sctlId);
		
		List<InterbankTransfer> ibtList = interBankTransferDao.get(query);
		
		if(ibtList!=null && !ibtList.isEmpty())
		{
			//Only there should be one record for a given sctld
			return ibtList.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public ServiceChargeTransactionLog getSctlByTransactionLogId(Long transactionLogId){
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getByTransactionLogId(transactionLogId);
		
		return sctl;
	}
}
