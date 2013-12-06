package com.mfino.zenith.airtime.visafone.impl;

import static com.mfino.mce.core.util.MCEUtil.isNullorZero;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;

import com.mfino.dao.AirtimePurchaseDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.AirtimePurchaseQuery;
import com.mfino.domain.AirtimePurchase;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePendingCommodityTransferRequest;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePurchase;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePurchaseInquiry;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.CommodityTransferService;
import com.mfino.mce.backend.impl.BackendServiceDefaultImpl;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.zenith.airtime.visafone.VisafoneAirtimeBankService;
import com.mfino.zenith.airtime.visafone.VisafoneAirtimeConstants;
import com.mfino.zenith.airtime.visafone.VisafoneAirtimePendingClearanceService;
import com.mfino.zenith.airtime.visafone.VisafoneAritimeService;

/**
 * 
 * @author Sasi
 *
 */
public class VisafoneAirtimeServiceImpl extends BackendServiceDefaultImpl implements VisafoneAritimeService {
	
	private VisafoneAirtimeBankService visafoneAirtimeBankService;
	
	private CommodityTransferService commodityTransferService;
	
	private VisafoneAirtimeTransactionIdGenerator txnIdGenerator;
	
	private VisafoneAirtimePendingClearanceService visafoneAirtimePendingClearanceService;
	
	@Override
	public MCEMessage processMessage(MCEMessage mceMessage) {
		log.info("VisafoneAirtimeServiceImpl :: processMessage() BEGIN "+mceMessage);
		
		if(visafoneAirtimeBankService==null){
			log.error("Error in VisafoneAirtimeServiceImpl, visafoneAirtimeBankService not set");
			VisafoneAirtimeBackendResponse response = (VisafoneAirtimeBackendResponse)createResponseObject();
			response.setInternalErrorCode(CmFinoFIX.NotificationCode_DependencyInjectionError);
			mceMessage.setResponse(response);
			return mceMessage;
		}
		
		if(txnIdGenerator == null){
			txnIdGenerator = new VisafoneAirtimeTransactionIdGenerator();
		}
		
		AirtimePurchaseDAO airtimePurchaseDAO = DAOFactory.getInstance().getAirtimePurchaseDao();
		
		try
		{
			CFIXMsg returnFix = preProcess(mceMessage);
			CMBase baseMessage =  getBaseMessage(mceMessage);
			CFIXMsg requestFix = (CFIXMsg)mceMessage.getRequest();
			CFIXMsg responseFix = (CFIXMsg)mceMessage.getResponse();

			if(isNullorZero(((BackendResponse)returnFix).getInternalErrorCode())){

				if(baseMessage instanceof CMVisafoneAirtimePurchaseInquiry){
					log.debug("VisafoneAirtimeServiceImpl baseMessage instanceof CMVisafoneAirtimePurchaseInquiry");
					CMVisafoneAirtimePurchaseInquiry paymentInquiry = (CMVisafoneAirtimePurchaseInquiry)baseMessage; 
					createAirtimePurchase(paymentInquiry);
					returnFix = visafoneAirtimeBankService.onVisafoneAirtimeTransferInquiryToBank(paymentInquiry);
					AirtimePurchase airtimePurchase = getAirtimePurchase(paymentInquiry.getServiceChargeTransactionLogID());
					
					if(returnFix instanceof BackendResponse)
					{
						BackendResponse backendResponse = (BackendResponse)returnFix;
						airtimePurchase.setResponseCode(backendResponse.getInternalErrorCode());
						if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Success)
						{
							airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_INQUIRY_COMPLETED);
						}else
						{
							airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_INQUIRY_FAILED);
						}
					}
					else if(returnFix instanceof CMVisafoneAirtimeTransferInquiryToBank)
					{
						airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_INQUIRY_COMPLETED);
					}
					
					airtimePurchaseDAO.save(airtimePurchase);
				}
				else if(baseMessage instanceof CMVisafoneAirtimeTransferInquiryFromBank){
					log.debug("VisafoneAirtimeServiceImpl baseMessage instanceof CMVisafoneAirtimeTransferInquiryFromBank");
					returnFix = visafoneAirtimeBankService.onVisafoneAirtimeTransferInquiryFromBank((CMVisafoneAirtimeTransferInquiryToBank)requestFix, (CMVisafoneAirtimeTransferInquiryFromBank)responseFix);
				}
				else if(baseMessage instanceof CMVisafoneAirtimePurchase){
					log.debug("VisafoneAirtimeServiceImpl baseMessage instanceof CMVisafoneAirtimePurchase");
					CMVisafoneAirtimePurchase payment = (CMVisafoneAirtimePurchase)baseMessage;
					
					AirtimePurchase airtimePurchase = getAirtimePurchase(payment.getServiceChargeTransactionLogID());
					airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_PAYMENT_REQUESTED);
					airtimePurchaseDAO.save(airtimePurchase);
					
					returnFix = visafoneAirtimeBankService.onVisafoneAirtimeTransferConfirmationToBank((CMVisafoneAirtimePurchase)baseMessage);
					
					if(returnFix instanceof BackendResponse){
						log.debug("VisafoneAirtimeServiceImpl :: returnFix instanceof BackendResponse");
						BackendResponse backendResponse = (BackendResponse)returnFix;
						airtimePurchase.setResponseCode(backendResponse.getInternalErrorCode());
						if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Success)
						{
							airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_PAYMENT_COMPLETED);
						}
						else
						{
							airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_PAYMENT_FAILED);
						}
						airtimePurchaseDAO.save(airtimePurchase);
						
						((BackendResponse) returnFix).setServiceChargeTransactionLogID(payment.getServiceChargeTransactionLogID());
					}
					if(returnFix instanceof VisafoneAirtimeBackendResponse)
					{
						log.debug("VisafoneAirtimeServiceImpl :: returnFix instanceof VisafoneAirtimeBackendResponse");
						VisafoneAirtimeBackendResponse vAirtimeBackendResponse = (VisafoneAirtimeBackendResponse)returnFix;
						
						Long pctID = vAirtimeBackendResponse.getTransferID();
						log.debug("VisafoneAirtimeServiceImpl :: vAirtimeBackendResponse.getTransferID()="+vAirtimeBackendResponse.getTransferID());
						
						if(pctID != null){
							PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
							PendingCommodityTransfer pct = pctDAO.getById(pctID);
							pct.setTransferStatus(CmFinoFIX.TransferStatus_RequestSentToIntegrationAPI);
							coreDataWrapper.save(pct);
						}
						//prepare the object for ws call
						vAirtimeBackendResponse.setAmount(airtimePurchase.getAmount().add(airtimePurchase.getCharges()));
						String txnId = txnIdGenerator.getTransactionId();
						log.debug("VisafoneAirtimeServiceImpl Generated Txn Id="+txnId);
						vAirtimeBackendResponse.setINTxnId(txnId);
						vAirtimeBackendResponse.setRechargeMdn(airtimePurchase.getRechargeMDN());
						vAirtimeBackendResponse.setAccountType(VisafoneAirtimeConstants.ACCOUNT_TYPE_PREPAID);
						airtimePurchase.setINTxnId(txnId);
						airtimePurchaseDAO.save(airtimePurchase);
					}
				}
				else if(baseMessage instanceof CMVisafoneAirtimeMoneyTransferFromBank){
					log.debug("VisafoneAirtimeServiceImpl baseMessage instanceof CMVisafoneAirtimeMoneyTransferFromBank");
					returnFix = visafoneAirtimeBankService.onVisafoneAirtimeTransferConfirmationFromBank((CMVisafoneAirtimeMoneyTransferToBank)requestFix, (CMVisafoneAirtimeMoneyTransferFromBank)responseFix);

					if(returnFix instanceof BackendResponse)
					{
						CMVisafoneAirtimeMoneyTransferFromBank moneyTrfFromBank = (CMVisafoneAirtimeMoneyTransferFromBank)baseMessage;
						AirtimePurchase airtimePurchase = getAirtimePurchase(moneyTrfFromBank.getServiceChargeTransactionLogID());
						BackendResponse backendResponse = (BackendResponse)returnFix;
						if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Success)
						{
							airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_PAYMENT_COMPLETED);
						}else
						{
							airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_PAYMENT_FAILED);
						}
						airtimePurchase.setResponseCode(backendResponse.getInternalErrorCode());
						airtimePurchaseDAO.save(airtimePurchase);
					}
					if(returnFix instanceof VisafoneAirtimeBackendResponse)
					{
						((VisafoneAirtimeBackendResponse)returnFix).setServiceChargeTransactionLogID(((CMVisafoneAirtimeMoneyTransferToBank)requestFix).getServiceChargeTransactionLogID());
					}
				}
				else if (baseMessage instanceof VisafoneAirtimeBackendResponse){
					log.debug("VisafoneAirtimeServiceImpl baseMessage instanceof VisafoneAirtimeBackendResponse");
					VisafoneAirtimeBackendResponse response = (VisafoneAirtimeBackendResponse)baseMessage;
					log.debug("VisafoneAirtimeServiceImpl :: response from web service = &&&"+response.getWebServiceResponse() + ", responsecode="+response.getResponseCode());
					PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
					
					Long pctID = response.getTransferID();
						
					if(pctID != null){
						PendingCommodityTransfer pct = pctDAO.getById(pctID);
						
						AirtimePurchase airtimePurchase = getAirtimePurchase(response.getServiceChargeTransactionLogID());
						
						if(VisafoneAirtimeResponseCodes.OPERATION_SUCCESSFUL.getInternalErrorCode().equals(Integer.valueOf(response.getWebServiceResponse())))
						{
							// web service returned success
							log.info("Web service returned success");
	
							pct.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
							pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly);
							pct.setNotificationCode(NotificationCodes.AirtimePurchaseConfirmation.getNotificationCode());
							pct.setEndTime(new Timestamp());
							commodityTransferService.movePctToCt(pct);
	
							airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_COMPLETED);
							airtimePurchase.setResponseCode(Integer.valueOf(response.getWebServiceResponse()));
							airtimePurchaseDAO.save(airtimePurchase);
							
							((BackendResponse)returnFix).setInternalErrorCode(NotificationCodes.AirtimePurchaseConfirmation.getInternalErrorCode());
							((BackendResponse)returnFix).setTransferID(pctID);
							
							BigDecimal amount = pct.getAmount();
							if(pct.getCharges() != null){
								amount = amount.add(pct.getCharges());
							}
							
							((BackendResponse)returnFix).setAmount(amount);
							((BackendResponse)returnFix).setCharges(BigDecimal.valueOf(0));
							((BackendResponse)returnFix).setCurrency(pct.getCurrency());
							((BackendResponse)returnFix).setSourceMDN(response.getSourceMDN());
							((BackendResponse)returnFix).setReceiverMDN(response.getRechargeMdn());
							((BackendResponse)returnFix).setResult(CmFinoFIX.ResponseCode_Success);
							((BackendResponse)returnFix).setServiceChargeTransactionLogID(airtimePurchase.getSctlId());
							
							((BackendResponse)returnFix).setSourceMDNBalance(response.getSourceMDNBalance());
							((BackendResponse)returnFix).setDestinationMDNBalance(response.getDestinationMDNBalance());
						}
						else{
							log.info("VisafoneAirtimeServiceImpl :: webservice failed response from web service="+response.getWebServiceResponse());
							
							if(MCEUtil.SERVICE_UNAVAILABLE.equals(response.getWebServiceResponse())){
								airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_SERVICE_UNAVAILABLE);
								airtimePurchase.setResponseCode(Integer.valueOf(response.getWebServiceResponse()));
								
								pct.setTransferStatus(CmFinoFIX.TransferStatus_IntegrationServiceUnavailable);
								pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_IntegrationServiceUnavailable);
								pct.setOperatorActionRequired(true);
								((BackendResponse)returnFix).setInternalErrorCode(NotificationCodes.AirtimePurchaseFailed.getInternalErrorCode());
							}
/*							
 * TODO : This line is commented as functionality is not required in new VTU interface spec document.
 * Delete these lines if not required. 
 * 							else if(((VisafoneAirtimeResponseCodes.INVALID_ACCOUNT.getInternalErrorCode().equals(Integer.valueOf(response.getWebServiceResponse())))) &&
									(VisafoneAirtimeConstants.ACCOUNT_TYPE_PREPAID.equals(response.getAccountType())))
							{
								log.info("VisafoneAirtimeServiceImpl :: Got invalid account for account type = prepaid, trying WS again with post paid account"+response.getWebServiceResponse());
								String txnId = txnIdGenerator.getTransactionId();
								response.setAccountType(VisafoneAirtimeConstants.ACCOUNT_TYPE_POSTPAID);
								response.setINTxnId(txnId);
								returnFix = response;
							}
*/							else{
								returnFix = visafoneAirtimeBankService.onRevertOfIntegrationService(pct);
								
								if(returnFix instanceof BackendResponse)
								{
									BackendResponse backendResponse = (BackendResponse)returnFix;
									
									if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Success){
										airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_COMPLETED);
										airtimePurchase.setResponseCode(backendResponse.getInternalErrorCode());
										((BackendResponse)returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
									}
									else if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Failure){
										airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_WEBSERVICE_FAILED);
										airtimePurchase.setResponseCode(backendResponse.getInternalErrorCode());
										((BackendResponse)returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
									}
								}
							}

							
							airtimePurchaseDAO.save(airtimePurchase);
							coreDataWrapper.save(pct);
							
							((BackendResponse)returnFix).setAmount(pct.getAmount());
							((BackendResponse)returnFix).setCharges(pct.getCharges());
							((BackendResponse)returnFix).setCurrency(pct.getCurrency());
							((BackendResponse)returnFix).setSourceMDN(response.getSourceMDN());
							((BackendResponse)returnFix).setReceiverMDN(response.getRechargeMdn());
							((BackendResponse)returnFix).setTransferID(response.getTransferID());
							((BackendResponse)returnFix).setServiceChargeTransactionLogID(airtimePurchase.getSctlId());
						}
					}
				}
				else if(baseMessage instanceof CMVisafoneAirtimeMoneyTransferReversalToBank){
					log.debug("VisafoneAirtimeServiceImpl baseMessage instanceof CMVisafoneAirtimeMoneyTransferReversalToBank");
				}
				else if(baseMessage instanceof CMVisafoneAirtimeMoneyTransferReversalFromBank){
					log.debug("VisafoneAirtimeServiceImpl baseMessage instanceof CMVisafoneAirtimeMoneyTransferReversalFromBank");
				}
				else if(baseMessage instanceof CMVisafoneAirtimePendingCommodityTransferRequest){
					log.debug("VisafoneAirtimeServiceImpl baseMessage instanceof CMVisafoneAirtimePendingCommodityTransferRequest");
					
					ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
					PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
					ServiceChargeTransactionLog sctl = sctlDao.getById(((CMVisafoneAirtimePendingCommodityTransferRequest)baseMessage).getTransferID());
					PendingCommodityTransfer pct = commodityTransferService.getPendingCT(sctl.getID());
					
					AirtimePurchase airtimePurchase = getAirtimePurchase(sctl.getID());
					
					returnFix = visafoneAirtimePendingClearanceService.processMessage((CMVisafoneAirtimePendingCommodityTransferRequest)baseMessage);
					
					if(returnFix instanceof BackendResponse)
					{
						BackendResponse backendResponse = (BackendResponse)returnFix;
						
						if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Success){
							airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_COMPLETED);
							airtimePurchase.setResponseCode(backendResponse.getInternalErrorCode());
							
							sctl.setStatus(CmFinoFIX.SCTLStatus_Confirmed);
						}
						else if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Failure){
							airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_WEBSERVICE_FAILED);
							airtimePurchase.setResponseCode(backendResponse.getInternalErrorCode());
							
							sctl.setStatus(CmFinoFIX.SCTLStatus_Failed);
						}
						
						airtimePurchaseDAO.save(airtimePurchase);
						sctlDao.save(sctl);
					}
				}
				else
				{
					log.error("VisafoneAirtimeServiceImpl :: got an invalid message "+requestFix.DumpFields());
					((BackendResponse)returnFix).setInternalErrorCode(NotificationCodes.InternalSystemError.getInternalErrorCode());  
				}
			}

			mceMessage = getResponse(mceMessage, returnFix);
		}
		catch(Exception e){
			log.error("Error in VisafoneAirtimeServiceImpl ", e);
		}
		
		if(mceMessage.getResponse() != null){
			log.debug("VisafoneAirtimeServiceImpl Return FIX "+mceMessage.getResponse().DumpFields());
		}
		
		log.info("VisafoneAirtimeServiceImpl :: processMessage() END return messsage="+mceMessage);
		
		return mceMessage;
	}

	protected void createAirtimePurchase(CMVisafoneAirtimePurchaseInquiry airtimePurchaseInquiry)
	{
		AirtimePurchase airtimePurchase = new AirtimePurchase();
		airtimePurchase.setAmount(airtimePurchaseInquiry.getAmount());
		airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_INITIALIZED);
		airtimePurchase.setCharges(airtimePurchaseInquiry.getCharges());
		airtimePurchase.setSctlId(airtimePurchaseInquiry.getServiceChargeTransactionLogID());
		airtimePurchase.setPartnerCode(airtimePurchaseInquiry.getPartnerCode());
		airtimePurchase.setINCode(airtimePurchaseInquiry.getINCode());
		airtimePurchase.setSourceMDN(airtimePurchaseInquiry.getSourceMDN());
		airtimePurchase.setINAccountType(CmFinoFIX.INAccountType_PREPAID);
		airtimePurchase.setRechargeMDN(airtimePurchaseInquiry.getRechargeMDN());
		
		AirtimePurchaseDAO airtimePurchaseDao = DAOFactory.getInstance().getAirtimePurchaseDao();
		airtimePurchaseDao.save(airtimePurchase);
	}

	protected AirtimePurchase getAirtimePurchase(Long sctlID)
	{
		AirtimePurchaseDAO airtimePurchaseDao = DAOFactory.getInstance().getAirtimePurchaseDao();
		AirtimePurchaseQuery query = new AirtimePurchaseQuery();
		query.setSctlID(sctlID);
		List<AirtimePurchase> airtimePurchaseList = airtimePurchaseDao.get(query);
		
		if(airtimePurchaseList!=null && !airtimePurchaseList.isEmpty())
		{
			//Only there should be one record for a given sctld
			return airtimePurchaseList.get(0);
		}
		
		return null;
	}
	
	public VisafoneAirtimeBankService getVisafoneAirtimeBankService() {
		return visafoneAirtimeBankService;
	}

	public void setVisafoneAirtimeBankService(VisafoneAirtimeBankService visafoneAirtimeBankService) {
		this.visafoneAirtimeBankService = visafoneAirtimeBankService;
	}

	public CommodityTransferService getCommodityTransferService() {
		return commodityTransferService;
	}

	public void setCommodityTransferService(CommodityTransferService commodityTransferService) {
		this.commodityTransferService = commodityTransferService;
	}

	public VisafoneAirtimeTransactionIdGenerator getTxnIdGenerator() {
		return txnIdGenerator;
	}

	public void setTxnIdGenerator(VisafoneAirtimeTransactionIdGenerator txnIdGenerator) {
		this.txnIdGenerator = txnIdGenerator;
	}

	public VisafoneAirtimePendingClearanceService getVisafoneAirtimePendingClearanceService() {
		return visafoneAirtimePendingClearanceService;
	}

	public void setVisafoneAirtimePendingClearanceService(VisafoneAirtimePendingClearanceService visafoneAirtimePendingClearanceService) {
		this.visafoneAirtimePendingClearanceService = visafoneAirtimePendingClearanceService;
	}
}

