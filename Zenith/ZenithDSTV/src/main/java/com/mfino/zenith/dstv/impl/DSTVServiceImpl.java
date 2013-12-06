package com.mfino.zenith.dstv.impl;

import static com.mfino.mce.core.util.MCEUtil.isNullorZero;

import java.math.BigDecimal;
import java.util.List;

import com.mfino.dao.BillPaymentsDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVPayment;
import com.mfino.fix.CmFinoFIX.CMDSTVPaymentInquiry;
import com.mfino.fix.CmFinoFIX.CMDSTVPendingCommodityTransferRequest;
import com.mfino.fix.CmFinoFIX.CMDSTVTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.CommodityTransferService;
import com.mfino.mce.backend.impl.BackendRuntimeException;
import com.mfino.mce.backend.impl.BackendServiceDefaultImpl;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.zenith.dstv.DSTVBackendResponse;
import com.mfino.zenith.dstv.DSTVBankService;
import com.mfino.zenith.dstv.DSTVPendingClearanceService;
import com.mfino.zenith.dstv.DSTVResponseUtil;
import com.mfino.zenith.dstv.DSTVService;

/**
 * @author Sasi
 *
 */
public class DSTVServiceImpl extends BackendServiceDefaultImpl implements DSTVService {

	private DSTVBankService dstvBankService;
	
	private CommodityTransferService commodityTransferService;
	
	private DSTVPendingClearanceService dstvPendingClearanceService;
	
	@Override
	public MCEMessage processMessage(MCEMessage mceMessage) throws BackendRuntimeException {
		log.info("DSTVServiceImpl :: processMessage() BEGIN "+mceMessage);
		if(dstvBankService==null){
			log.error("Error in DSTVService, DSTVBankService not set");
			DSTVBackendResponse response = (DSTVBackendResponse)createResponseObject();
			response.setInternalErrorCode(CmFinoFIX.NotificationCode_DependencyInjectionError);
			mceMessage.setResponse(response);
			return mceMessage;
		}

		try
		{
			CFIXMsg returnFix = preProcess(mceMessage);
			CMBase baseMessage =  getBaseMessage(mceMessage);
			CFIXMsg requestFix = (CFIXMsg)mceMessage.getRequest();
			CFIXMsg responseFix = (CFIXMsg)mceMessage.getResponse();

			if(isNullorZero(((BackendResponse)returnFix).getInternalErrorCode())){

				if(baseMessage instanceof CMDSTVPaymentInquiry){
					CMDSTVPaymentInquiry paymentInquiry = (CMDSTVPaymentInquiry)baseMessage; 
					createBillPaymentsRecord(paymentInquiry);
					returnFix = getDstvBankService().onDSTVTransferInquiryToBank(paymentInquiry);
					BillPayments billPayments = getBillPaymentsRecord(paymentInquiry.getServiceChargeTransactionLogID());
					BillPaymentsDAO billPaymentsDAO = DAOFactory.getInstance().getBillPaymentDAO();
					if(returnFix instanceof BackendResponse)
					{
						BackendResponse backendResponse = (BackendResponse)returnFix;
						billPayments.setResponseCode(backendResponse.getInternalErrorCode());
						if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Success)
						{
							billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_INQUIRY_COMPLETED);
						}else
						{
							billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_INQUIRY_FAILED);
						}
					}else if(returnFix instanceof CMDSTVTransferInquiryToBank)
					{
						billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_INQUIRY_COMPLETED);
					}
					billPaymentsDAO.save(billPayments);
				}
				else if(baseMessage instanceof CMDSTVMoneyTransferReversalToBank)
				{
					// when there is no response from the bank this message is received
					// send a backend response so that notification can handle accordingly.
					// returnFix = getDstvBankService().onDSTVTransferReversalToBank((CMBankAccountToBankAccountConfirmation)requestFix, (CMDSTVMoneyTransferReversalToBank)responseFix);
					//TODO: we get a DSTVBackendResponse, convert it to BackendResponse and send it for
					// notification to handle accordingly
					((CMBase)returnFix).copy(((CMBase)requestFix));
					BackendResponse outResponse = new BackendResponse();
					((CMBase)outResponse).copy(((CMBase)returnFix));
					returnFix = outResponse;
					//TODO: set the other required feilds for the notification to work correctly
				}
				else if(baseMessage instanceof CMDSTVMoneyTransferReversalFromBank)
				{
					log.warn("reversal is not implemented for zenith so this message should not have come");
				}
				else if(baseMessage instanceof CMDSTVTransferInquiryFromBank){
					returnFix = getDstvBankService().onDSTVTransferInquiryFromBank((CMDSTVTransferInquiryToBank)requestFix, (CMDSTVTransferInquiryFromBank)responseFix);
				}
				else if(baseMessage instanceof CMDSTVPayment){
					CMDSTVPayment payment = (CMDSTVPayment)baseMessage;
					BillPayments billPayments = getBillPaymentsRecord(payment.getServiceChargeTransactionLogID());
					BillPaymentsDAO billPaymentsDAO = DAOFactory.getInstance().getBillPaymentDAO();
					billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_PAYMENT_REQUESTED);
					billPaymentsDAO.save(billPayments);
					returnFix = getDstvBankService().onDSTVTransferConfirmationToBank((CMDSTVPayment)baseMessage);
					if(returnFix instanceof BackendResponse)
					{
						BackendResponse backendResponse = (BackendResponse)returnFix;
						((BackendResponse) returnFix).setBillerCode(billPayments.getBillerCode());
						billPayments.setResponseCode(backendResponse.getInternalErrorCode());						
						if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Success)
						{
							billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_PAYMENT_COMPLETED);
						}else
						{
							billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_PAYMENT_FAILED);
						}
						billPaymentsDAO.save(billPayments);
					}
				}
				else if(baseMessage instanceof CMDSTVMoneyTransferFromBank){
					returnFix = getDstvBankService().onDSTVTransferConfirmationFromBank((CMDSTVMoneyTransferToBank)requestFix, (CMDSTVMoneyTransferFromBank)responseFix);

					if(returnFix instanceof BackendResponse)
					{
						CMDSTVMoneyTransferFromBank moneyTrfFromBank = (CMDSTVMoneyTransferFromBank)baseMessage;
						BillPayments billPayments = getBillPaymentsRecord(moneyTrfFromBank.getServiceChargeTransactionLogID());
						BillPaymentsDAO billPaymentsDAO = DAOFactory.getInstance().getBillPaymentDAO();
						BackendResponse backendResponse = (BackendResponse)returnFix;
						if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Success)
						{
							billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_PAYMENT_COMPLETED);
						}else
						{
							billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_PAYMENT_FAILED);
						}
						billPayments.setResponseCode(backendResponse.getInternalErrorCode());
						billPaymentsDAO.save(billPayments);
					}
					if(returnFix instanceof DSTVBackendResponse)
					{
						((DSTVBackendResponse)returnFix).setDecoderCode(((CMDSTVMoneyTransferToBank)requestFix).getInvoiceNumber());
						((DSTVBackendResponse)returnFix).setInvoiceNumber(((CMDSTVMoneyTransferToBank)requestFix).getInvoiceNumber());
						
						log.debug("DSTVServiceImpl :: ((DSTVBackendResponse)returnFix).getInvoiceNumber()==>>"+((DSTVBackendResponse)returnFix).getInvoiceNumber());
						
						Long pctID = ((DSTVBackendResponse)returnFix).getTransferID();
						log.debug("DSTVServiceImpl ::  ((DSTVBackendResponse)returnFix).getTransferID()="+pctID);
						
						if(pctID != null){
							PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
							PendingCommodityTransfer pct = pctDAO.getById(pctID);
							pct.setTransferStatus(CmFinoFIX.TransferStatus_RequestSentToIntegrationAPI);
							coreDataWrapper.save(pct);
						}
					}
				}
				else if (baseMessage instanceof DSTVBackendResponse)
				{
					PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
					DSTVBackendResponse response = (DSTVBackendResponse)baseMessage;
					Long pctID = response.getTransferID();
					PendingCommodityTransfer pct = pctDAO.getById(pctID);
					String onBehalfOfMDN = getServiceChargeTransactionLog(response.getServiceChargeTransactionLogID()).getOnBeHalfOfMDN();
					
					if(DSTVResponseUtil.isSuccess(((DSTVBackendResponse)baseMessage).getWebServiceResponse()))
					{
						// web service returned success
						log.info("Web service returned success");
						

						pct.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
						pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly);
						pct.setNotificationCode(NotificationCodes.BillPayCompletedToSender.getNotificationCode());
						pct.setEndTime(new Timestamp());
						commodityTransferService.movePctToCt(pct);
						
						//log response.getSctlID()
						BillPayments billPayments = getBillPaymentsRecord(response.getServiceChargeTransactionLogID());
						BillPaymentsDAO billPaymentsDAO = DAOFactory.getInstance().getBillPaymentDAO();
						BackendResponse backendResponse = (BackendResponse)returnFix;

						billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_COMPLETED);

						billPayments.setResponseCode(backendResponse.getInternalErrorCode());
						billPaymentsDAO.save(billPayments);
						
						((BackendResponse)returnFix).setInternalErrorCode(NotificationCodes.BillPayCompletedToSender.getInternalErrorCode());
						((BackendResponse)returnFix).setTransferID(pctID);
						((BackendResponse)returnFix).setBillerCode(billPayments.getBillerCode());
						((BackendResponse)returnFix).setInvoiceNumber(billPayments.getInvoiceNumber());
						
						BigDecimal totalAmount = new BigDecimal(0);
						BigDecimal amount = pct.getAmount();
						BigDecimal charges = pct.getCharges();
						if(amount != null){
							totalAmount = totalAmount.add(amount);
						}
						if(charges != null){
							totalAmount = totalAmount.add(charges);
						}
						
						((BackendResponse)returnFix).setAmount(totalAmount);
						((BackendResponse)returnFix).setCharges(new BigDecimal(0));
						((BackendResponse)returnFix).setCurrency(pct.getCurrency());
						((BackendResponse)returnFix).setResult(CmFinoFIX.ResponseCode_Success);
						
						((BackendResponse)returnFix).setSourceMDN(response.getSourceMDN());
						((BackendResponse)returnFix).setReceiverMDN(response.getReceiverMDN());
						((BackendResponse)returnFix).setSenderMDN(response.getSenderMDN());
						((BackendResponse)returnFix).setOnBehalfOfMDN(onBehalfOfMDN);

						((BackendResponse)returnFix).setSourceMDNBalance(response.getSourceMDNBalance());
						((BackendResponse)returnFix).setDestinationMDNBalance(response.getDestinationMDNBalance());
					}
					else
					{
						log.info("Web service returned failure or didnt respond");
						
						// web service returned failure  
						BillPayments billPayments = getBillPaymentsRecord(response.getServiceChargeTransactionLogID());
						BillPaymentsDAO billPaymentsDAO = DAOFactory.getInstance().getBillPaymentDAO();
												BackendResponse backendResponse = (BackendResponse)returnFix;

						billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_WEBSERVICE_FAILED);
						Integer retryCount = billPayments.getNoOfRetries();
						if(retryCount==null){
							retryCount = new Integer(0);
						}
						retryCount++;
						billPayments.setNoOfRetries(retryCount);
						billPayments.setResponseCode(backendResponse.getInternalErrorCode());
						billPaymentsDAO.save(billPayments);
						
						pct.setTransferStatus(CmFinoFIX.TransferStatus_ResponseReceivedFromIntegrationAPI);
						pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_RejectedByIntegration);
						pct.setOperatorActionRequired(true);
//						pct.setNotificationCode(notificationcode)
						coreDataWrapper.save(pct);
						
						((BackendResponse)returnFix).setInternalErrorCode(NotificationCodes.BillPayPending.getInternalErrorCode());
						((BackendResponse)returnFix).setBillerCode(billPayments.getBillerCode());
						((BackendResponse)returnFix).setInvoiceNumber(billPayments.getInvoiceNumber());
						((BackendResponse)returnFix).setAmount(pct.getAmount());
						((BackendResponse)returnFix).setCharges(pct.getCharges());
						((BackendResponse)returnFix).setCurrency(pct.getCurrency());
						((BackendResponse)returnFix).setOnBehalfOfMDN(onBehalfOfMDN);
					}
				}
				else if (baseMessage instanceof CMDSTVPendingCommodityTransferRequest){
					log.info("DSTVServiceImpl :: processMessage() baseMessage instanceof CMDSTVPendingCommodityTransferRequest");
					
					ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
//					BillPaymentsDAO billPaymentsDAO = DAOFactory.getInstance().getBillPaymentDAO();
					PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
					
//					PendingCommodityTransfer pct = pctDAO.getById(((CMDSTVPendingCommodityTransferRequest)baseMessage).getTransferID());
					ServiceChargeTransactionLog sctl = sctlDao.getById(((CMDSTVPendingCommodityTransferRequest)baseMessage).getTransferID());
//					BillPayments billPayments = getBillPaymentsRecord(sctl.getID()); 
//					BackendResponse backendResponse = (BackendResponse)returnFix;
					
					BillPayments billPayments = getBillPaymentsRecord(sctl.getID());
					
					returnFix = dstvPendingClearanceService.processMessage((CMDSTVPendingCommodityTransferRequest)baseMessage);
					
					if(returnFix instanceof BackendResponse)
					{
						
						BillPaymentsDAO billPaymentsDAO = DAOFactory.getInstance().getBillPaymentDAO();
						BackendResponse backendResponse = (BackendResponse)returnFix;
						if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Revert_Success)
						{
							billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_PAYMENT_COMPLETED);
						}
						else if(backendResponse.getResult()==CmFinoFIX.ResponseCode_Success){
							//FIXME: need better way to identify state of transaction
							//was bank transfer resolved or integration status resolved
							billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_COMPLETED);
							
							sctl.setStatus(CmFinoFIX.SCTLStatus_Confirmed);
							sctl.setFailureReason(""+backendResponse.getInternalErrorCode());
							sctlDao.save(sctl);
						}
						else
						{
							billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_PAYMENT_FAILED);
						}
						
						billPayments.setResponseCode(backendResponse.getInternalErrorCode());
						billPaymentsDAO.save(billPayments);
						
						
						((BackendResponse)returnFix).setBillerCode(billPayments.getBillerCode());
						((BackendResponse)returnFix).setInvoiceNumber(billPayments.getInvoiceNumber());
					}
					if(returnFix instanceof DSTVBackendResponse)
					{
						((DSTVBackendResponse)returnFix).setDecoderCode(billPayments.getInvoiceNumber());
						((DSTVBackendResponse)returnFix).setAmount(billPayments.getAmount());
//						((DSTVBackendResponse)returnFix).setTransactionID(pct.getTransactionsLogByTransactionID().getid);
						
						Long pctID = ((DSTVBackendResponse)returnFix).getTransferID();
						log.debug("DSTVServiceImpl ::  ((DSTVBackendResponse)returnFix).getTransferID()="+pctID);
						PendingCommodityTransfer pct;
						if(pctID != null){
							pct = pctDAO.getById(pctID);
							pct.setTransferStatus(CmFinoFIX.TransferStatus_RequestSentToIntegrationAPI);
							coreDataWrapper.save(pct);
						}
					}
				}
				else
				{
					log.error("got an invalid message "+requestFix.DumpFields());
					((BackendResponse)returnFix).setInternalErrorCode(NotificationCodes.InternalSystemError.getInternalErrorCode());  
				}
			}

			mceMessage = getResponse(mceMessage, returnFix);
		}
		catch(Exception e){
			log.error("Error in DSTVService ", e);
		}

		if(mceMessage.getResponse() != null){
			log.debug("Return FIX "+mceMessage.getResponse().DumpFields());
		}
		log.info("DSTVServiceImpl :: processMessage() END return messsage="+mceMessage);
		return mceMessage;
	}

	/**
	 * @return the dstvBankService
	 */
	public DSTVBankService getDstvBankService() {
		return dstvBankService;
	}

	/**
	 * @param dstvBankService the dstvBankService to set
	 */
	public void setDstvBankService(DSTVBankService dstvBankService) {
		this.dstvBankService = dstvBankService;
	}

	public BackendResponse createResponseObject() 
	{
		return new DSTVBackendResponse();
	}

	protected void createBillPaymentsRecord(CMDSTVPaymentInquiry dstvPaymentInquiry)
	{
		BillPayments billPayments = new BillPayments();
		billPayments.setAmount(dstvPaymentInquiry.getAmount());
		billPayments.setBillerCode(dstvPaymentInquiry.getBillerCode());
		billPayments.setBillPayStatus(CmFinoFIX.BillPayStatus_INITIALIZED);
		billPayments.setCharges(dstvPaymentInquiry.getCharges());
		billPayments.setInvoiceNumber(dstvPaymentInquiry.getInvoiceNumber());
		billPayments.setSctlId(dstvPaymentInquiry.getServiceChargeTransactionLogID());

		BillPaymentsDAO billPayDAO = DAOFactory.getInstance().getBillPaymentDAO();
		billPayDAO.save(billPayments);
	}

	protected BillPayments getBillPaymentsRecord(Long sctlID)
	{
		BillPaymentsDAO billPayDAO = DAOFactory.getInstance().getBillPaymentDAO();
		BillPaymentsQuery query = new BillPaymentsQuery();
		query.setSctlID(sctlID);
		List<BillPayments> billPaymentsList = billPayDAO.get(query);
		if(billPaymentsList!=null && !billPaymentsList.isEmpty())
		{
			//Only there should be one record for a given sctld
			return billPaymentsList.get(0);
		}
		return null;
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

	public DSTVPendingClearanceService getDstvPendingClearanceService() {
		return dstvPendingClearanceService;
	}

	public void setDstvPendingClearanceService(DSTVPendingClearanceService dstvPendingClearanceService) {
		this.dstvPendingClearanceService = dstvPendingClearanceService;
	}
	
	public ServiceChargeTransactionLog getServiceChargeTransactionLog(Long Id){
		ServiceChargeTransactionLogDAO billPayDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		return billPayDAO.getById(Id);
	}
}
