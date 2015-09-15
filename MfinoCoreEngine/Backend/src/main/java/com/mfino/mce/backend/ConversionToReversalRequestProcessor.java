package com.mfino.mce.backend;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.InterBankTransfersDao;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.InterbankTransfer;
import com.mfino.domain.NoISOResponseMsg;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentReversalToBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentToBank;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class ConversionToReversalRequestProcessor {
	Log	log	= LogFactory.getLog(ConversionToReversalRequestProcessor.class);

	public CFIXMsg processMessage(CFIXMsg requestFixMsg, NoISOResponseMsg responseFix ) {
		// request that was sent to frontend for outside communication
		//		 = mesg.getResponse();

		// request is a money transfer to bank then we need to send
		// a reversal request to bank
		if (requestFixMsg instanceof CMDSTVMoneyTransferToBank) {
			log.info("no response from bank for DSTV, constructing a reversal fix message");

			String str = "";

			CMDSTVMoneyTransferToBank moneyTransferToBank = (CMDSTVMoneyTransferToBank) requestFixMsg;
			CMDSTVMoneyTransferReversalToBank reversalFixMsg = new CMDSTVMoneyTransferReversalToBank();
			reversalFixMsg.copy(moneyTransferToBank);

			/**
			 * Set the mandatary parameters for the fix message.
			 */
			reversalFixMsg.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFixMsg.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFixMsg.setBankSystemTraceAuditNumber((moneyTransferToBank.getTransactionID() % 1000000) + "");
			reversalFixMsg.setTransactionID(reversalFixMsg.getTransactionID() + 1);
			reversalFixMsg.setBankRetrievalReferenceNumber(reversalFixMsg.getTransactionID() + "");

			return reversalFixMsg;

		}
		else if (requestFixMsg instanceof CMQRPaymentToBank) {
			log.info("Constructing the QR Payment reversal fix msg....");
			CMQRPaymentToBank qrPaymentToBank = (CMQRPaymentToBank)requestFixMsg;
			CMQRPaymentReversalToBank billPayrevtobank = new CMQRPaymentReversalToBank();
			billPayrevtobank.copy(qrPaymentToBank);
			billPayrevtobank.header().setSendingTime(DateTimeUtil.getLocalTime());
			billPayrevtobank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			billPayrevtobank.setBankSystemTraceAuditNumber((qrPaymentToBank.getTransactionID() % 1000000) + "");
			billPayrevtobank.setTransactionID(responseFix.getTransactionID());
			billPayrevtobank.setBankRetrievalReferenceNumber(billPayrevtobank.getTransactionID() + "");			
			if(qrPaymentToBank.getBillerPartnerType().equals(CmFinoFIX.BillerPartnerType_Topup_Denomination) || 
					qrPaymentToBank.getBillerPartnerType().equals(CmFinoFIX.BillerPartnerType_Topup_Free)){
				billPayrevtobank.setProcessingCode("56");
			}else{
				billPayrevtobank.setProcessingCode("50");
			}
			billPayrevtobank.setBillerCode(qrPaymentToBank.getBillerCode());
			return billPayrevtobank;
		}
		// also write for reversal also not replied by bank
		else if (requestFixMsg instanceof CMMoneyTransferToBank) {
			log.info("no response from bank, constructing reversal fix message ");
			CMMoneyTransferToBank moneyTransferToBank = (CMMoneyTransferToBank) requestFixMsg;
			CMMoneyTransferReversalToBank reversalFixMsg = new CMMoneyTransferReversalToBank();
			reversalFixMsg.copy(moneyTransferToBank);

			/**
			 * Set the mandatary parameters for the fix message.
			 */
			reversalFixMsg.header().setSendingTime(DateTimeUtil.getLocalTime());
			reversalFixMsg.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			reversalFixMsg.setBankSystemTraceAuditNumber((moneyTransferToBank.getTransactionID() % 1000000) + "");
			// get the transaction id from the NOISOResponse
			reversalFixMsg.setTransactionID(responseFix.getTransactionID());
			reversalFixMsg.setBankRetrievalReferenceNumber(reversalFixMsg.getTransactionID() + "");

			// Setting DestBankCode separately as it is not part of MoneyTransferToBank - Hence would not copy from original message
			InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
			InterBankTransfersQuery query = new InterBankTransfersQuery();
			query.setSctlId(moneyTransferToBank.getServiceChargeTransactionLogID());
			List<InterbankTransfer> ibtList = interBankTransferDao.get(query);

			if(ibtList!=null && !ibtList.isEmpty())
			{
				//Only there should be one record for a given sctld
				reversalFixMsg.setDestBankCode(ibtList.get(0).getDestBankCode());
			}
			constructAndSetDE3(reversalFixMsg);
			return reversalFixMsg;
		}
		else {
			log.warn("Not sure why this message is here, some bug in routing logic");
		}
		return null;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	private void constructAndSetDE3(CMMoneyTransferReversalToBank reversalFixMsg){
		String defaultDE3=ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;
		if (CmFinoFIX.BankAccountType_Saving.toString().equals(reversalFixMsg.getSourceBankAccountType())){
			defaultDE3="49"+CmFinoFIX.BankAccountCardType_SavingsAccount.toString()+"00"; ;
		}
		else if (CmFinoFIX.BankAccountType_Checking.toString().equals(reversalFixMsg.getSourceBankAccountType())){
			defaultDE3="49"+CmFinoFIX.BankAccountCardType_CheckingAccount.toString()+"00";
		}

		reversalFixMsg.setProcessingCodeDE3(defaultDE3);// default de-3 will be overwritten based on destination ac type
		log.info("MoneyTransferReversalToBankProcessor :: process default " + defaultDE3);
		String processingCode=null;
		IntegrationSummaryDao isDAO  = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummaryQuery isQuery = new IntegrationSummaryQuery();
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		ServiceChargeTransactionLog sctl = null;
		Long transferID = reversalFixMsg.getTransferID();
		log.info("MoneyTransferReversalToBankProcessor :: transferID msg.getTransferID()"+transferID);
		TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
		IntegrationSummary iSummary = null;
		Long sctlID;
		String reconciliationID1 = null;
		if(transferID!=null){
			sctlQuery.setTransferID(transferID);
			log.info("MoneyTransferReversalToBankProcessor :: process Transfer ID :"+transferID);
			List<ServiceChargeTransactionLog> list= sctlDAO.get(sctlQuery);
			if(CollectionUtils.isNotEmpty(list)){
				sctl = list.get(0);
				sctlID = sctl.getID();
				log.info("MoneyTransferReversalToBankProcessor :: process Sctl ID :"+sctlID);
				isQuery.setSctlID(sctlID);
				List<IntegrationSummary> isList = isDAO.get(isQuery);
				if(CollectionUtils.isNotEmpty(isList)){
					iSummary = isList.get(0);
					reconciliationID1 = iSummary.getReconcilationID1();
					log.info("MoneyTransferReversalToBankProcessor :: process ReconciliationID1 :"+reconciliationID1);
					log.info("MoneyTransferReversalToBankProcessor :: Dumping message fields :" + reversalFixMsg.DumpFields());
					log.info("MoneyTransferReversalToBankProcessor :: SourceMDN " + reversalFixMsg.getSourceMDN());
					//log.info("MoneyTransferReversalToBankProcessor :: source Pocket" + sourcePocket.DumpFields());
					if(StringUtils.isNotBlank(reconciliationID1))
					{
						if (CmFinoFIX.BankAccountType_Saving.toString().equals(reversalFixMsg.getSourceBankAccountType())){
							processingCode="49"+CmFinoFIX.BankAccountCardType_SavingsAccount.toString()+reconciliationID1 ;
						}else if (CmFinoFIX.BankAccountType_Checking.toString().equals(reversalFixMsg.getSourceBankAccountType())){
							processingCode="49"+CmFinoFIX.BankAccountCardType_CheckingAccount.toString()+reconciliationID1;
						}
						log.info("MoneyTransferReversalToBankProcessor :: process Setting ProcessingCode :"+processingCode+" in DE-3");
						reversalFixMsg.setProcessingCodeDE3(defaultDE3);
					}

				}	   		
			}
		}
	}
}
