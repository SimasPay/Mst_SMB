package com.mfino.sterling.interbank.impl;

import static com.mfino.sterling.bank.util.SterlingBankWebServiceIntegrationConstants.INTERBANK_PARTNER_MDN_KEY;
import static com.mfino.sterling.bank.util.SterlingBankWebServiceIntegrationConstants.NARRATION;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.InterBankTransfersDao;
import com.mfino.dao.InterbankCodesDao;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.SystemParametersDao;
import com.mfino.dao.query.InterBankCodesQuery;
import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.domain.InterBankCode;
import com.mfino.domain.InterbankTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransfer;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.mce.backend.impl.BankServiceDefaultImpl;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.SubscriberService;
import com.mfino.sterling.interbank.IBTBankService;

/**
 * 
 * @author Amar
 *
 */
public class IBTBankServiceImpl extends BankServiceDefaultImpl implements IBTBankService {
	
	private SubscriberService subscriberService;

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public CFIXMsg onTransferInquiry(CMInterBankFundsTransferInquiry ibtInquiry) {
		log.debug("IBTBankServiceImpl :: onTransferInquiry() BEGIN");
		
		CFIXMsg returnFix = null;
		
		Pocket sourcePocket = coreDataWrapper.getPocketById(ibtInquiry.getSourcePocketID());
		
		Pocket destinationPocket = getIBDestinationPocket(); 
		
		ibtInquiry.setDestPocketID(destinationPocket.getID());
		
		InterbankTransfer ibt = createInterBankTransfer(ibtInquiry, sourcePocket);
		
		InterBankCode nbCode = getBankCode(ibtInquiry.getDestBankCode());
		
		boolean isIBTAllowed = ((nbCode != null) && (nbCode.getibAllowed())) ? true : false;

		if(!isIBTAllowed){
			log.debug("Inter Bank transfer restricted bank code="+ibtInquiry.getDestBankCode());
			ibt.setIBTStatus(CmFinoFIX.IBTStatus_FAILED);
			updateIBT(ibt);
			
			BackendResponse response = new BackendResponse();
			response.setInternalErrorCode(NotificationCodes.IBTRestricted.getInternalErrorCode());
			response.setBankName(nbCode != null ? nbCode.getBankName() : ibtInquiry.getDestBankCode());	
			return response;
		}
		CFIXMsg inquiryToBankResponse=createResponseObject();
		try{
		inquiryToBankResponse = onTransferInquiryToBank(ibtInquiry);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) inquiryToBankResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) inquiryToBankResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if(inquiryToBankResponse instanceof CMTransferInquiryToBank){
			log.debug("IBTBankServiceImpl :: onTransferInquiry() toBankResponse instanceof CMTransferInquiryToBank");
			CMTransferInquiryToBank inquiryToBank = (CMTransferInquiryToBank)inquiryToBankResponse;
			CMInterBankTransferInquiryToBank interBankInquiryToBank = new  CMInterBankTransferInquiryToBank();
			interBankInquiryToBank.copy(inquiryToBank);
			interBankInquiryToBank.setDestBankCode(ibtInquiry.getDestBankCode());
			returnFix = interBankInquiryToBank;
		}
		else{
			log.debug("IBTBankServiceImpl :: onTransferInquiry() else block");
			//object will be of type BackendResponse
			returnFix = inquiryToBankResponse;
		}
		return returnFix;
		
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public CFIXMsg onTransferConfirmation(CMInterBankFundsTransfer ibtConfirmation) {
		log.debug("IBTBankServiceImpl :: onTransferConfirmation() BEGIN");
		
		CFIXMsg returnFix = null;
		
		InterbankTransfer ibt = getIBT(ibtConfirmation.getServiceChargeTransactionLogID());
		PendingCommodityTransfer pct = getPCT(ibtConfirmation.getTransferID());
		ibt.setPaymentReference(""+pct.getID());
		
		if(pct != null){
			ibtConfirmation.setDestMDN(pct.getDestMDN());
			ibtConfirmation.setDestPocketID(pct.getDestPocketID());
		}
		CFIXMsg confirmationToBankResponse=createResponseObject();
		try{
		confirmationToBankResponse = onTransferConfirmationToBank(ibtConfirmation);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) confirmationToBankResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) confirmationToBankResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if(confirmationToBankResponse instanceof CMMoneyTransferToBank){
			ibt.setIBTStatus(CmFinoFIX.IBTStatus_PROCESSING);
			
			CMInterBankMoneyTransferToBank moneyTransferToBank = new CMInterBankMoneyTransferToBank();
			
			moneyTransferToBank.copy(ibtConfirmation);
			
			moneyTransferToBank.setTerminalID(ibt.getTerminalID());
			moneyTransferToBank.setDestBankCode(ibt.getDestBankCode());
			moneyTransferToBank.setSourceAccountName(ibt.getSourceAccountName());
			moneyTransferToBank.setDestAccountName(ibt.getDestAccountName());
			moneyTransferToBank.setSourceAccountNumber(ibt.getSourceAccountNumber());
			moneyTransferToBank.setDestAccountNumber(ibt.getDestAccountNumber());
			moneyTransferToBank.setNarration(ibt.getNarration());
			moneyTransferToBank.setAmount(ibt.getAmount());
			moneyTransferToBank.setServiceChargeTransactionLogID(ibtConfirmation.getServiceChargeTransactionLogID());
			moneyTransferToBank.setDestPocketID(ibtConfirmation.getDestPocketID());
			moneyTransferToBank.setSourcePocketID(ibtConfirmation.getSourcePocketID());
			moneyTransferToBank.setSourceMDN(pct.getSourceMDN());
			moneyTransferToBank.setDestMDN(pct.getDestMDN());
			moneyTransferToBank.setTransferID(ibtConfirmation.getTransferID());
			moneyTransferToBank.setPin(((CMMoneyTransferToBank) confirmationToBankResponse).getPin());
			moneyTransferToBank.setBankCode(((CMMoneyTransferToBank) confirmationToBankResponse).getBankCode());
			
			returnFix = moneyTransferToBank;
		}
		else{
			/*
			 * Otherwise it should be BackendResponse.
			 * Do nothing in case of backend response.
			 * Confirmation to bank failed and notification will be returned.
			 */
			ibt.setIBTStatus(CmFinoFIX.IBTStatus_FAILED);
			returnFix = confirmationToBankResponse;
		}
		
		updateIBT(ibt);
		coreDataWrapper.save(pct);
		
		log.debug("IBTBankServiceImpl :: onTransferConfirmation() END");
		return returnFix;
	}
	
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public CFIXMsg onInterBankTransferInquiryFromBank(CMInterBankTransferInquiryToBank toBank, CMInterBankTransferInquiryFromBank fromBank){
		log.info("IBTBankServiceImpl :: onInterBankTransferInquiryFromBank() BEGIN");
		
		Long transferId = null;

		BackendResponse fromBankResponse=createResponseObject();
		if(fromBank.getResponseCode().equals(CmFinoFIX.ISO8583_ResponseCode_Success))
		{
			try{
				fromBankResponse = (BackendResponse)onTransferInquiryFromBank(toBank, fromBank);
			}catch(Exception e){
				log.error(e.getMessage());
				((BackendResponse) fromBankResponse).setResult(CmFinoFIX.ResponseCode_Failure);
				((BackendResponse) fromBankResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
			transferId = fromBankResponse.getTransferID();
		}
		
		InterbankTransfer ibt = getIBT(fromBank.getServiceChargeTransactionLogID());
		
		if(transferId != null){
			PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
			PendingCommodityTransfer pct = pctDAO.getById(transferId);
			
			if(pct != null){
				pct.setDestCardPAN(getIBDestinationPocket().getCardPAN());
				coreDataWrapper.save(pct);
				
				ibt.setTransferID(pct.getID());
				ibt.setPaymentReference(""+pct.getID());
				updateIBT(ibt);
			}
		}
		
		fromBankResponse.setDestBankAccountNumber(ibt.getDestAccountNumber());
		fromBankResponse.setSourceBankAccountNumber(ibt.getSourceAccountNumber());
		
		if(CmFinoFIX.ResponseCode_Success.equals(fromBankResponse.getResult())){
			fromBankResponse.setInternalErrorCode(NotificationCodes.IBTInquiry.getInternalErrorCode());
		}
		
		log.debug("IBTBankServiceImpl :: onInterBankTransferInquiryFromBank() END");
		return fromBankResponse;
	}
	
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public CFIXMsg onResponseFromInterBankService(CMInterBankMoneyTransferToBank toBank, CMInterBankMoneyTransferFromBank fromBank){
		log.info("IBTBankServiceImpl :: onResponseFromInterBankService() BEGIN");
		CFIXMsg returnFix = createResponseObject();
		
		InterbankTransfer ibt = getIBT(fromBank.getServiceChargeTransactionLogID());
		
		if(fromBank.getResponseCode().equals(CmFinoFIX.ISO8583_ResponseCode_Success)){
			ibt.setIBTStatus(CmFinoFIX.IBTStatus_COMPLETED);
		}
		else{
			ibt.setIBTStatus(CmFinoFIX.IBTStatus_FAILED);
		}
		
		if(!((MCEUtil.SERVICE_UNAVAILABLE.equals(fromBank.getResponseCode())) /*|| ((NIBSSResponseCode.RESPONSE_RECEIVED_TOO_LATE.getResponseCode().equals(fromBank.getResponseCode())))*/)){
			try{
			returnFix = onTransferConfirmationFromBank(toBank, fromBank);
			}catch(Exception e){
				log.error(e.getMessage());
				if(returnFix instanceof BackendResponse){
				((BackendResponse) returnFix).copy(toBank);
				((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
				((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
				}
			}
		}
		
		ibt.setSessionID(fromBank.getSessionID());
		ibt.setDestAccountName(fromBank.getDestAccountName());
		ibt.setSourceAccountName(fromBank.getSourceAccountName());
		ibt.setNIBResponseCode(fromBank.getResponseCode());
		
		updateIBT(ibt);
		
		BackendResponse backendResponse = new BackendResponse();
		backendResponse.setDestBankAccountNumber(ibt.getDestAccountNumber());
		backendResponse.setSourceBankAccountNumber(ibt.getSourceAccountNumber());
		
		if(returnFix != null){
			backendResponse.copy((BackendResponse)returnFix);
			
			if(CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)returnFix).getResult())){
				backendResponse.setInternalErrorCode(NotificationCodes.IBTConfirmation.getInternalErrorCode());
			}
			else{
				backendResponse.setInternalErrorCode(NotificationCodes.IBTFailed.getInternalErrorCode());
			}
		}
		if((MCEUtil.SERVICE_UNAVAILABLE.equals(fromBank.getResponseCode())) /*|| (NIBSSResponseCode.RESPONSE_RECEIVED_TOO_LATE.getResponseCode().equals(fromBank.getResponseCode()))*/){
			backendResponse.setInternalErrorCode(NotificationCodes.IBTPending.getInternalErrorCode());
		}
		
		log.info("IBTBankServiceImpl :: onResponseFromInterBankService() END");
		return backendResponse;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public InterbankTransfer createInterBankTransfer(CMInterBankFundsTransferInquiry ibtInquiry, Pocket sourcePocket){
		InterbankTransfer ibt = new InterbankTransfer();

		ibt.setDestBankCode(ibtInquiry.getDestBankCode());
		Subscriber srcSubscriber = sourcePocket.getSubscriberMDNByMDNID().getSubscriber();
		if(srcSubscriber != null)
		{
			String subscriberName = srcSubscriber.getFirstName() + " " + srcSubscriber.getLastName();
			ibt.setSourceAccountName(subscriberName);
		}
		ibt.setDestAccountName(ibtInquiry.getDestAccountNumber());
		ibt.setSourceAccountNumber(sourcePocket.getCardPAN());
		ibt.setDestAccountNumber(ibtInquiry.getDestAccountNumber());
		ibt.setNarration(NARRATION);
		ibt.setAmount(ibtInquiry.getAmount());
		ibt.setCharges(ibtInquiry.getCharges());
		ibt.setIBTStatus(CmFinoFIX.IBTStatus_INQUIRY);
		ibt.setSctlId(ibtInquiry.getServiceChargeTransactionLogID());

		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		interBankTransferDao.save(ibt);
		
		return ibt;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public InterbankTransfer updateIBT(InterbankTransfer ibt){
		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		interBankTransferDao.save(ibt);
		
		return ibt;
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
	public Pocket getIBDestinationPocket(){
		Pocket pocket = null;
		
		SystemParametersDao systemParameterDao = DAOFactory.getInstance().getSystemParameterDao();
		String interbankPartnerMdn = systemParameterDao.getSystemParameterByName(INTERBANK_PARTNER_MDN_KEY).getParameterValue();
		
		SubscriberMDNDAO subscriberMdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN subscriberMdn = subscriberMdnDao.getByMDN(interbankPartnerMdn);
		
		pocket = subscriberService.getDefaultPocket(subscriberMdn.getID(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		
		return pocket;
	}
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public ServiceChargeTransactionLog getSctlByTransactionLogId(Long transactionLogId){
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getByTransactionLogId(transactionLogId);
		
		return sctl;
	}

	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public ServiceChargeTransactionLog updateSctl(ServiceChargeTransactionLog sctl){
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		sctlDao.save(sctl);
		return sctl;
	}
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public ServiceChargeTransactionLog getSctlById(Long sctlId){
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getById(sctlId);
		
		return sctl;
	}
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public PendingCommodityTransfer getPCT(Long transferId){
		PendingCommodityTransferDAO pctDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
		return pctDao.getById(transferId);
	}
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public InterBankCode getBankCode(String bankCode){
		
		InterBankCode nbCode = null;
		InterbankCodesDao nbDao = DAOFactory.getInstance().getInterbankCodesDao();
		InterBankCodesQuery query = new InterBankCodesQuery();
		query.setBankCode(bankCode);
		List<InterBankCode> nbCodeList = nbDao.get(query);
		if(nbCodeList.size() >0){
			nbCode = nbCodeList.get(0);
		}
		
		return nbCode;
	}
}
