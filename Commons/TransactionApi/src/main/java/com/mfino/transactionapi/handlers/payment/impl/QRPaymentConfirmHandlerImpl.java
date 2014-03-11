/**
 * 
 */
package com.mfino.transactionapi.handlers.payment.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMQRPayment;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.BillPaymentsService;
import com.mfino.service.BillerService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.MFAService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.payment.BillPayConfirmHandler;
import com.mfino.transactionapi.handlers.payment.QRPaymentConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * 
 * @author HemanthKumar
 *
 */
@Service("QRPaymentConfirmHandlerImpl")
public class QRPaymentConfirmHandlerImpl extends FIXMessageHandler implements QRPaymentConfirmHandler{

	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	
	@Autowired
	@Qualifier("BillerServiceImpl")
	private BillerService billerService;
	
	@Autowired
	@Qualifier("BillPaymentsServiceImpl")
	private BillPaymentsService billPaymentsService;	

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	private static Logger log	= LoggerFactory.getLogger(QRPaymentConfirmHandlerImpl.class);
	private String serviceName = ServiceAndTransactionConstants.SERVICE_PAYMENT;
	private String transactionName = ServiceAndTransactionConstants.TRANSACTION_QR_PAYMENT;

	public Result handle(TransactionDetails transactionDetails) {
		 String transactionOtp = 	transactionDetails.getTransactionOTP();
		 String srcPocketCode  =   	transactionDetails.getSourcePocketCode();

		 CMQRPayment qrPayment = new CMQRPayment();
		 ChannelCode cc = transactionDetails.getCc();
		
		 qrPayment.setSourceMDN(transactionDetails.getSourceMDN());
		 qrPayment.setInvoiceNumber(transactionDetails.getBillNum());
		 qrPayment.setParentTransactionID(transactionDetails.getParentTxnId());
		 qrPayment.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		 qrPayment.setTransferID(transactionDetails.getTransferId());
		 qrPayment.setConfirmed(Boolean.parseBoolean(transactionDetails.getConfirmString()));
		 qrPayment.setSourceApplication(cc.getChannelSourceApplication());
		 qrPayment.setChannelCode(cc.getChannelCode());
		 qrPayment.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		 qrPayment.setBillerCode(transactionDetails.getBillerCode());
		 qrPayment.setNarration(transactionDetails.getNarration());
		 qrPayment.setPaymentMode(transactionDetails.getPaymentMode());
		 qrPayment.setUserAPIKey(transactionDetails.getUserAPIKey());
		 qrPayment.setMerchantData(transactionDetails.getMerchantData());
		log.info("Handling Subscriber qr payment confirmation WebAPI request");
		XMLResult result = new MoneyTransferXMLResult();
		//2FA
		ServiceChargeTransactionLog sctlForMFA = sctlService.getByTransactionLogId(qrPayment.getParentTransactionID());
		if(mfaService.isMFATransaction(serviceName, transactionName, cc.getID())){
			if(transactionOtp == null || !(mfaService.isValidOTP(transactionOtp,sctlForMFA.getID(), qrPayment.getSourceMDN()))){
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidData);
				return result;
			}
		}

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_QRPayment,qrPayment.DumpFields(),qrPayment.getParentTransactionID());
		qrPayment.setTransactionID(transactionsLog.getID());

		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(qrPayment);
		result.setTransactionID(qrPayment.getTransactionID());
		SubscriberMDN sourceMDN = subscriberMdnService.getByMDN(qrPayment.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+qrPayment.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		Pocket subPocket = pocketService.getDefaultPocket(sourceMDN, srcPocketCode);
		validationResult = transactionApiValidationService.validateSourcePocket(subPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(subPocket!=null? subPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(subPocket);
		result.setPocketList(pocketList);
        
		Partner partner = billerService.getPartner(qrPayment.getBillerCode());


		SubscriberMDN partnerMDN = partner.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
		validationResult = transactionApiValidationService.validatePartnerMDN(partnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination partner has failed validations");
			validationResult = processValidationResultForPartner(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}


		qrPayment.setDestMDN(partnerMDN.getMDN());
		qrPayment.setSourcePocketID(subPocket.getID());
		qrPayment.setSourceApplication(cc.getChannelSourceApplication());
		qrPayment.setEmail(sourceMDN.getSubscriber().getEmail());
		
		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(qrPayment.getParentTransactionID(),qrPayment.getTransactionIdentifier());
			
		
		if (sctl != null) {
			qrPayment.setServiceChargeTransactionLogID(sctl.getID());
			BillPaymentsQuery bpquery = new BillPaymentsQuery();
			bpquery.setSctlID(sctl.getID());
			List<BillPayments> res = billPaymentsService.get(bpquery);
			if(res.size() > 0){
				qrPayment.setIntegrationCode(res.get(0).getIntegrationCode());
				qrPayment.setPartnerBillerCode(res.get(0).getPartnerBillerCode());
				Iterator<MFSBillerPartner> mfsBillers=partner.getMFSBillerPartnerFromPartnerID().iterator();
				while(mfsBillers.hasNext()){
					MFSBillerPartner mfsbiller = mfsBillers.next();
					if(mfsbiller.getMFSBiller().getMFSBillerCode().equals(qrPayment.getBillerCode())){
						qrPayment.setBillerPartnerType(mfsbiller.getBillerPartnerType());
						break;
					}
				}
			}
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordChangedStatus);
				return result;
			}

		} else {
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}		
		Pocket destPocket;
		PartnerServices partnerService = transactionChargingService.getPartnerService(partner.getID(), sctl.getServiceProviderID(), sctl.getServiceID());
		if (partnerService == null) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
			return result;
		}
		destPocket = partnerService.getPocketByDestPocketID();
		validationResult = transactionApiValidationService.validateSourcePocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(destPocket!=null? destPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
	
		qrPayment.setDestPocketID(destPocket.getID());

		CFIXMsg response = super.process(qrPayment);
		result.setMultixResponse(response);
		commodityTransferService.addCommodityTransferToResult(result);
		result.setSctlID(sctl.getID());
		
		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {
				transactionChargingService.confirmTheTransaction(sctl, qrPayment.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, qrPayment.getTransferID());
				result.setDebitAmount(sctl.getTransactionAmount());
				result.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
				result.setAdditionalInfo(transactionResponse.getAdditionalInfo());
				result.setServiceCharge(sctl.getCalculatedCharge());
			} else {
				String errorMsg = transactionResponse.getMessage();
				// As the length of the Failure reason column is 255, we are trimming the error message to 255 characters.
				if (errorMsg.length() > 255) {
					errorMsg = errorMsg.substring(0, 255);
				}
				transactionChargingService.failTheTransaction(sctl, errorMsg);
			}
		}

		return result;
	}
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}

}