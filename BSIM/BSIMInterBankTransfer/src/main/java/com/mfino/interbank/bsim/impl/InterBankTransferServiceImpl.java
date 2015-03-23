package com.mfino.interbank.bsim.impl;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationSummaryDao;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.TransactionChargeLogDAO;
import com.mfino.dao.query.IntegrationSummaryQuery;
import com.mfino.domain.ChargeType;
import com.mfino.domain.IntegrationSummary;
import com.mfino.domain.InterBankCode;
import com.mfino.domain.InterbankTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.TransactionCharge;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransfer;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.interbank.bsim.InterBankTransferService;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.mce.core.util.StringUtilities;
import com.mfino.mce.interbank.InterBankService;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

public class InterBankTransferServiceImpl implements InterBankTransferService{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected BankService bankService;
	protected String sourceToDestInquiryQueue = "jms:sourceToDestIBTInquiryQueue?disableReplyTo=true";
	protected String sourceToDestQueue = "jms:sourceToDestIBTQueue?disableReplyTo=true";
	
	private InterBankService interbankService;
	
	//blunders :(
//	protected  CMMoneyTransferToBank confirmResponse;
//	protected String AdditionalInfo48="";
//	private String processingCode="";
//	private String sourceAccount="";
//	private String destAccount="";
//	private String destCardPan="";
//	private String bankCode="";
//	Long transferId;

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage ibtInquirySourceToDestination(MCEMessage mceMessage) {
		log.info("InterBankTransferServiceImpl :: interBankMoneyTransferInquirySourceToDestination mceMessage="+mceMessage);
		CMInterBankFundsTransferInquiry interBankFundsTransferInquiry = (CMInterBankFundsTransferInquiry)mceMessage.getRequest();

		InterBankCode interBankCode = interbankService.getBankCode(interBankFundsTransferInquiry.getDestBankCode());
		String bankCode = interBankFundsTransferInquiry.getDestBankCode();
//		Pocket destinationPocket = interbankService.getIBDestinationPocket();
		
		if(!interbankService.isIBTRestricted(bankCode)){
			log.info("Inter Bank transfer restricted bank code="+bankCode);
			
			BackendResponse response = new BackendResponse();
			response.setInternalErrorCode(NotificationCodes.IBTRestricted.getInternalErrorCode());
			response.setBankName(interBankCode != null ? interBankCode.getBankName() : bankCode);
			mceMessage.setResponse(response);
			return mceMessage;
		}
		
		Pocket sourcePocket = getPocketFromId(interBankFundsTransferInquiry.getSourcePocketID());
		InterbankTransfer ibt = interbankService.createInterBankTransfer(interBankFundsTransferInquiry, sourcePocket, interBankCode);
		
//		interBankFundsTransferInquiry.setDestPocketID(destinationPocket.getID());
		interBankFundsTransferInquiry.setMessageType(CmFinoFIX.MsgType_InterBankFundsTransferInquiry);
		interBankFundsTransferInquiry.setParentTransactionID(0L);
		
		CFIXMsg inqResponse;
		try {
		inqResponse = bankService.onTransferInquiryToBank(interBankFundsTransferInquiry);
		} catch(Exception e){
			log.error(e.getMessage());
			inqResponse = new BackendResponse();
			((BackendResponse) inqResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) inqResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		mceMessage.setResponse(inqResponse);
		
		if(inqResponse instanceof CMTransferInquiryToBank){
			CMTransferInquiryToBank inquiryResponse =(CMTransferInquiryToBank) inqResponse;
			CMInterBankTransferInquiryToBank response = new CMInterBankTransferInquiryToBank();
			Long sctlID = inquiryResponse.getServiceChargeTransactionLogID();
			TransactionChargeLogDAO tclDAO = DAOFactory.getInstance().getTransactionChargeLogDAO();
			
			BigDecimal serviceCharge = new BigDecimal(0);
			BigDecimal tax = new BigDecimal(0);
			
			List <TransactionChargeLog> tclList = tclDAO.getBySCTLID(sctlID);
			if(CollectionUtils.isNotEmpty(tclList)){
				for(Iterator<TransactionChargeLog> it = tclList.iterator();it.hasNext();){
					TransactionChargeLog tcl = it.next();
					TransactionCharge txnCharge=tcl.getTransactionCharge();
					ChargeType chargeType = txnCharge.getChargeType();
					String chargeTypeName = chargeType.getName();
					if(chargeTypeName.equalsIgnoreCase("charge")){
						serviceCharge = tcl.getCalculatedCharge();
					}
					if(chargeTypeName.equalsIgnoreCase("tax")){
						tax = tcl.getCalculatedCharge();
					}				
				}
			}
			
			response.setServiceChargeDE63(serviceCharge.toBigInteger().toString());
			response.setTaxAmount(tax);
			
			String mdn = StringUtilities.leftPadWithCharacter(inquiryResponse.getSourceMDN(), 13, "0");
			String mdngen = MfinoUtil.CheckDigitCalculation(mdn); 
			response.setSourceMDN(inquiryResponse.getSourceMDN());
			response.setMPan(mdngen);
			response.setLanguage(inquiryResponse.getLanguage());
			response.setAmount(inquiryResponse.getAmount());
			response.setBankCode(inquiryResponse.getBankCode());
			response.setDestMDN(inquiryResponse.getDestMDN());
			response.setParentTransactionID(inquiryResponse.getParentTransactionID());
			response.setUICategory(inquiryResponse.getUICategory());
			response.setSourcePocketID(inquiryResponse.getSourcePocketID());
			response.setDestPocketID(inquiryResponse.getDestPocketID());
			response.setSourceCardPAN(inquiryResponse.getSourceCardPAN());
			response.setDestCardPAN(interBankFundsTransferInquiry.getDestAccountNumber());
			response.setDestinationBankAccountNo(inquiryResponse.getDestinationBankAccountNo());
			response.setTransferID(inquiryResponse.getTransferID());
			response.setTransactionID(inquiryResponse.getTransactionID());
			response.setParentTransactionID(inquiryResponse.getParentTransactionID());
			response.setSourceBankAccountType(inquiryResponse.getSourceBankAccountType());
			response.setDestinationBankAccountType(inquiryResponse.getDestinationBankAccountType());
			response.setServiceChargeTransactionLogID(inquiryResponse.getServiceChargeTransactionLogID());
			response.setPin(inquiryResponse.getPin());
			response.setDestBankCode(interBankFundsTransferInquiry.getDestBankCode());
			response.setServiceChargeAmount(interBankFundsTransferInquiry.getCharges());
			mceMessage.setResponse(response);
		}
		
		mceMessage.setRequest(interBankFundsTransferInquiry);
		mceMessage.setDestinationQueue(sourceToDestInquiryQueue);

		//saveIntegrationSummary(interBankFundsTransferInquiry.getTransactionID().toString(), inquiryResponse.getServiceChargeTransactionLogID());

		return mceMessage;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage ibtInquiryCompletionSourceToDestination(MCEMessage mceMessage) {
		//FIXME: What ever need to be saved as part of ISO response is saved in BankService - Should come here ideally
		
		log.info("InterBankTransferServiceImpl :: interBankFundsTransferInquiryCompletionSourceToDestination mceMessage="+mceMessage);
		CMInterBankTransferInquiryToBank interBankTransferInquiryToBank = (CMInterBankTransferInquiryToBank)mceMessage.getRequest();
		CMInterBankTransferInquiryFromBank interbankTransferInquiryFromBank = (CMInterBankTransferInquiryFromBank)mceMessage.getResponse();
		
		InterbankTransfer ibt = interbankService.getIBT(interBankTransferInquiryToBank.getServiceChargeTransactionLogID());
		BackendResponse inquiryResponse;
		try {
			inquiryResponse = (BackendResponse) bankService.onTransferInquiryFromBank(interBankTransferInquiryToBank,interbankTransferInquiryFromBank);
			if (CmFinoFIX.ISO8583_ResponseCode_Success.equals(interbankTransferInquiryFromBank.getResponseCode())) {
				// updating the destination account name in interbank transfer table
				ibt.setDestAccountName(inquiryResponse.getDestinationUserName());
				interbankService.updateIBT(ibt);
			} 
			else {
				ibt.setIBTStatus(CmFinoFIX.IBTStatus_FAILED);
			}
		} catch(Exception e){
			log.error(e.getMessage());
			inquiryResponse = new BackendResponse();
			((BackendResponse) inquiryResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) inquiryResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			ibt.setIBTStatus(CmFinoFIX.IBTStatus_FAILED);
		}
		interbankService.updateIBT(ibt);
		 //processingCode = interbankTransferInquiryFromBank.getProcessingCode();
		//sourceAccount = interbankTransferInquiryFromBank.getSourceAccountDE102();
		//AdditionalInfo48 = interbankTransferInquiryFromBank.getAdditionalInfo();
		//destAccount = interbankTransferInquiryFromBank.getDestinationAccountDE103();
		
		//ibt.setAdditionalInfo(interbankTransferInquiryFromBank.getAdditionalInfo());
		//interbankService.updateIBT(ibt);
		
		if(null != ibt){
			InterBankCode interBankCode = interbankService.getBankCode(ibt.getDestBankCode());
			if(null != interBankCode){
				inquiryResponse.setBankName(interBankCode.getBankName());
			}
		}
		
		mceMessage.setRequest(interbankTransferInquiryFromBank);
		mceMessage.setResponse(inquiryResponse);
		return mceMessage;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage ibtSourceToDestination(MCEMessage mceMessage) {
		
		log.info("InterBankTransferServiceImpl :: interBankFundsTransferSourceToDestination mceMessage="+mceMessage);
		
		CMInterBankFundsTransfer interBankFundsTransfer = (CMInterBankFundsTransfer)mceMessage.getRequest();
		PendingCommodityTransfer pct = getPCT(interBankFundsTransfer.getTransferID());
		IntegrationSummary iSummary = getIntegrationSummary(interBankFundsTransfer.getServiceChargeTransactionLogID(),pct.getID());
		
		InterbankTransfer ibt = interbankService.getIBT(interBankFundsTransfer.getServiceChargeTransactionLogID());
		// update the IBT status to processing
		ibt.setIBTStatus(CmFinoFIX.IBTStatus_PROCESSING);
		ibt.setSourceAccountName(pct.getSourceSubscriberName());
		interbankService.updateIBT(ibt);
		
		interBankFundsTransfer.setDestMDN(pct.getDestMDN());
		interBankFundsTransfer.setDestPocketID(pct.getDestPocketID());
		
		CFIXMsg cnfResponse;
		try {
			cnfResponse= bankService.onTransferConfirmationToBank(interBankFundsTransfer);
		} catch(Exception e){
			log.error(e.getMessage());
			cnfResponse = new BackendResponse();
			((BackendResponse) cnfResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) cnfResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		mceMessage.setResponse(cnfResponse);
		
		if(cnfResponse instanceof CMMoneyTransferToBank){
			
			CMMoneyTransferToBank confirmResponse = (CMMoneyTransferToBank)cnfResponse;
			
			CMInterBankMoneyTransferToBank response = new CMInterBankMoneyTransferToBank();
			String mdn = StringUtilities.leftPadWithCharacter(confirmResponse.getSourceMDN(), 13, "0");
			String mdngen = MfinoUtil.CheckDigitCalculation(mdn); 
			
			Long sctlID = confirmResponse.getServiceChargeTransactionLogID();
			TransactionChargeLogDAO tclDAO = DAOFactory.getInstance().getTransactionChargeLogDAO();
			
			BigDecimal serviceCharge = new BigDecimal(0);
			BigDecimal tax = new BigDecimal(0);
			
			List <TransactionChargeLog> tclList = tclDAO.getBySCTLID(sctlID);
			if(CollectionUtils.isNotEmpty(tclList)){
				for(Iterator<TransactionChargeLog> it = tclList.iterator();it.hasNext();){
					TransactionChargeLog tcl = it.next();
					if(tcl.getTransactionCharge().getChargeType().getName().equalsIgnoreCase("charge")){
						serviceCharge = tcl.getCalculatedCharge();
					}
					if(tcl.getTransactionCharge().getChargeType().getName().equalsIgnoreCase("tax")){
						tax = tcl.getCalculatedCharge();
					}				
				}
			}
			
			response.setServiceChargeAmount(serviceCharge);
			response.setTaxAmount(tax);
			response.setSourceMDN(confirmResponse.getSourceMDN());
			response.setMPan(mdngen);
			response.setAmount(confirmResponse.getAmount());
			response.setBankCode(confirmResponse.getBankCode());
			response.setDestCardPAN(confirmResponse.getDestCardPAN());
			response.setDestMDN(confirmResponse.getDestMDN());
			response.setParentTransactionID(confirmResponse.getParentTransactionID());
			response.setUICategory(confirmResponse.getUICategory());
			response.setSourcePocketID(confirmResponse.getSourcePocketID());
			response.setDestPocketID(confirmResponse.getDestPocketID());
			response.setPin(confirmResponse.getPin());
		    response.setDestAccountName(iSummary.getReconcilationID2());
			response.setSourceCardPAN(confirmResponse.getSourceCardPAN());
			response.setDestCardPAN(ibt.getDestAccountNumber());
			response.setLanguage(confirmResponse.getLanguage());
			response.setTransferID(confirmResponse.getTransferID());
			response.setTransactionID(confirmResponse.getTransactionID());
			//response.setTransferTime(confirmResponse.getTransferTime()); 
			Timestamp ts = DateTimeUtil.getGMTTime();
			response.setTransferTime(ts);
			response.setDestinationBankAccountType(confirmResponse.getDestinationBankAccountType());
			response.setOriginalReferenceID(confirmResponse.getOriginalReferenceID());
			response.setSourceBankAccountType(confirmResponse.getSourceBankAccountType());
			response.setDestinationBankAccountType(confirmResponse.getDestinationBankAccountType());
			response.setServiceChargeTransactionLogID(confirmResponse.getServiceChargeTransactionLogID());
			response.setProcessingCode(iSummary.getReconcilationID1());
			StringBuffer sb = new StringBuffer(iSummary.getReconcilationID2());
			sb = sb.replace(46, 76, StringUtilities.rightPadWithCharacter(pct.getSourceSubscriberName(), 30, " "));
	        response.setAdditionalInfo(sb.toString());
	        response.setDestBankCode(ibt.getDestBankCode());
			mceMessage.setRequest(interBankFundsTransfer);
			mceMessage.setDestinationQueue(sourceToDestQueue);
			mceMessage.setResponse(response);		
		}
		
		return mceMessage;
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage ibtCompletionSourceToDestination(MCEMessage mceMessage) {
		
		log.info("InterBankTransferServiceImpl :: interBankFundsTransferCompletionSourceToDestination mceMessage="+mceMessage);
		
		CMInterBankMoneyTransferToBank interBankFundsTransferToBank = (CMInterBankMoneyTransferToBank)mceMessage.getRequest();
		CMInterBankMoneyTransferFromBank interBankFundsTransferFromBank = (CMInterBankMoneyTransferFromBank) mceMessage.getResponse();

		InterbankTransfer ibt = interbankService.getIBT(interBankFundsTransferToBank.getServiceChargeTransactionLogID());
		
		CFIXMsg response;
		try {
		response = bankService.onTransferConfirmationFromBank(interBankFundsTransferToBank,interBankFundsTransferFromBank);
		if (CmFinoFIX.ISO8583_ResponseCode_Success.equals(interBankFundsTransferFromBank.getResponseCode())) {
			ibt.setIBTStatus(CmFinoFIX.IBTStatus_COMPLETED);
		}
		else {
			ibt.setIBTStatus(CmFinoFIX.IBTStatus_FAILED);
		}
		} catch(Exception e){
			log.error(e.getMessage());
			response = new BackendResponse();
			((BackendResponse) response).copy(interBankFundsTransferToBank);
			((BackendResponse) response).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) response).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			ibt.setIBTStatus(CmFinoFIX.IBTStatus_FAILED);
		}
		//Updating the IBT status after processing the bank response code.
		interbankService.updateIBT(ibt);
		CFIXMsg request = (CMMoneyTransferFromBank)interBankFundsTransferFromBank;
		
		((BackendResponse)response).setDestinationType("Account");
		((BackendResponse)response).setReceiverMDN(ibt.getDestAccountNumber());
		
		mceMessage.setRequest(request);
		mceMessage.setResponse(response);
		saveIntegrationSummary(interBankFundsTransferToBank.getSctlId(),interBankFundsTransferFromBank.getResponseCode());
	
		return mceMessage;
	}
	
	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}
	
	private void saveIntegrationSummary(Long sctlId, String de39){
		
		IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummaryQuery query = new IntegrationSummaryQuery();
		query.setSctlID(sctlId);
		List<IntegrationSummary> iSummaryList = integrationSummaryDao.get(query);

		IntegrationSummary iSummary = null;
		if((null != iSummaryList)&&(iSummaryList.size() > 0)){
			iSummary = iSummaryList.get(0);
			iSummary.setReconcilationID2(de39);
		}
		else{
			iSummary = new IntegrationSummary();
			iSummary.setSctlId(sctlId);
			iSummary.setReconcilationID2(de39);
		}

		integrationSummaryDao.save(iSummary);
	}
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public Pocket getPocketFromId(Long pocketId){
		PocketDAO dao = DAOFactory.getInstance().getPocketDAO();

		Pocket pocket = dao.getById(pocketId);

		return pocket;
	}
	
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public PendingCommodityTransfer getPCT(Long transferId){
		PendingCommodityTransferDAO pctDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
		return pctDao.getById(transferId);
	}

	public InterBankService getInterbankService() {
		return interbankService;
	}

	public void setInterbankService(InterBankService interbankService) {
		this.interbankService = interbankService;
	}

	private IntegrationSummary getIntegrationSummary(Long sctlId, Long pctId){
		IntegrationSummaryDao integrationSummaryDao = DAOFactory.getInstance().getIntegrationSummaryDao();
		IntegrationSummary iSummary = integrationSummaryDao.getByScltId(sctlId,pctId);
		return iSummary;
	}
}
