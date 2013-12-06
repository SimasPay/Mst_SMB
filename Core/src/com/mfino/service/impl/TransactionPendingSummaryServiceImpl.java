package com.mfino.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionPendingSummaryDAO;
import com.mfino.domain.TransactionPendingSummary;
import com.mfino.fix.CmFinoFIX.CMBillPayPendingRequest;
import com.mfino.fix.CmFinoFIX.CMInterBankPendingCommodityTransferRequest;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.TransactionPendingSummaryService;

@Service("TransactionPendingSummaryServiceImpl")
public class TransactionPendingSummaryServiceImpl implements TransactionPendingSummaryService{
	
	private static Logger log = LoggerFactory.getLogger(TransactionPendingSummaryServiceImpl.class);
	TransactionPendingSummaryDAO transactionPendingSummaryDAO = DAOFactory.getInstance().getTransactionPendingSummaryDAO();

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public TransactionPendingSummary saveTransactionPendingSummary( CMPendingCommodityTransferRequest newMsg){
		
		log.info("Trying to Save TransactionPendingSummary Domain Object corresponding to SCTL with id:"+newMsg.getServiceChargeTransactionLogID()+" while Resolving");
	
		TransactionPendingSummary transactionPendingSummary = new TransactionPendingSummary();
		transactionPendingSummary.setSctlId(newMsg.getServiceChargeTransactionLogID());
		transactionPendingSummary.setCSRAction(newMsg.getCSRAction());
		transactionPendingSummary.setCSRActionTime(new Timestamp());
		transactionPendingSummary.setCSRComment(newMsg.getCSRComment());
		transactionPendingSummary.setCSRUserID(newMsg.getCSRUserID());
		transactionPendingSummary.setCSRUserName(newMsg.getCSRUserName());
		
		transactionPendingSummaryDAO.save(transactionPendingSummary);
		log.info("Successfully saved TransactionPendingSummary Domain Object with Id:"+transactionPendingSummary.getID()+" corresponding to SCTL with id:"+newMsg.getServiceChargeTransactionLogID()+" while Resolving");
		
		return transactionPendingSummary;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public TransactionPendingSummary saveTransactionPendingSummary(CMBillPayPendingRequest newMsg){
		log.info("Trying to Save TransactionPendingSummary Domain Object corresponding to SCTL with id:"+newMsg.getServiceChargeTransactionLogID()+" while Resolving");
		
		TransactionPendingSummary transactionPendingSummary = new TransactionPendingSummary();
		transactionPendingSummary.setSctlId(newMsg.getServiceChargeTransactionLogID());
		transactionPendingSummary.setCSRAction(newMsg.getCSRAction());
		transactionPendingSummary.setCSRActionTime(new Timestamp());
		transactionPendingSummary.setCSRComment(newMsg.getCSRComment());
		transactionPendingSummary.setCSRUserID(newMsg.getCSRUserID());
		transactionPendingSummary.setCSRUserName(newMsg.getCSRUserName());
		
		transactionPendingSummaryDAO.save(transactionPendingSummary);
		log.info("Successfully saved TransactionPendingSummary Domain Object with Id:"+transactionPendingSummary.getID()+" corresponding to SCTL with id:"+newMsg.getServiceChargeTransactionLogID()+" while Resolving");
		
		return transactionPendingSummary;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public TransactionPendingSummary saveTransactionPendingSummary(CMInterBankPendingCommodityTransferRequest newMsg){
		log.info("Trying to Save TransactionPendingSummary Domain Object corresponding to SCTL with id:"+newMsg.getServiceChargeTransactionLogID()+" while Resolving");
		
		TransactionPendingSummary transactionPendingSummary = new TransactionPendingSummary();
		transactionPendingSummary.setSctlId(newMsg.getServiceChargeTransactionLogID());
		transactionPendingSummary.setCSRAction(newMsg.getCSRAction());
		transactionPendingSummary.setCSRActionTime(new Timestamp());
		transactionPendingSummary.setCSRComment(newMsg.getCSRComment());
		transactionPendingSummary.setCSRUserID(newMsg.getCSRUserID());
		transactionPendingSummary.setCSRUserName(newMsg.getCSRUserName());
		
		transactionPendingSummaryDAO.save(transactionPendingSummary);
		log.info("Successfully saved TransactionPendingSummary Domain Object with Id:"+transactionPendingSummary.getID()+" corresponding to SCTL with id:"+newMsg.getServiceChargeTransactionLogID()+" while Resolving");
		
		return transactionPendingSummary;
	}
}
