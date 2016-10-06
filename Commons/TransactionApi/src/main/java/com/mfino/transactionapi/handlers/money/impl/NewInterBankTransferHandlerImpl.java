/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

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
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.BillPaymentsService;
import com.mfino.service.BillerService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.MFAService;
import com.mfino.service.MFSBillerPartnerMapService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.InterBankTransferHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * 
 * @author HemanthKumar
 *
 */
@Service("NewInterBankTransferHandlerImpl")
public class NewInterBankTransferHandlerImpl extends FIXMessageHandler implements InterBankTransferHandler{

	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	private static Logger log = LoggerFactory.getLogger(NewInterBankTransferHandlerImpl.class);
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("MFSBillerPartnerMapServiceImpl")
	private MFSBillerPartnerMapService mfsBillerPartnerMapService;
	

	@Autowired
	@Qualifier("BillPaymentsServiceImpl")
	private BillPaymentsService billPaymentsService;	

	@Autowired
	@Qualifier("BillerServiceImpl")
	private BillerService billerService;
	
	private String serviceName = ServiceAndTransactionConstants.SERVICE_WALLET;
	private String transactionName = ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER;
	
	public Result handle(TransactionDetails transactionDetails) {
		 String transactionOtp = 	transactionDetails.getTransactionOTP();
		 String srcPocketCode  =   	transactionDetails.getSourcePocketCode();

		 CMBillPay ibtConfirm = new CMBillPay();
		 ChannelCode cc = transactionDetails.getCc();
		
		 ibtConfirm.setSourceMDN(transactionDetails.getSourceMDN());
		 ibtConfirm.setParentTransactionID(transactionDetails.getParentTxnId());
		 ibtConfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		 ibtConfirm.setTransferID(transactionDetails.getTransferId());
		 ibtConfirm.setConfirmed(Boolean.parseBoolean(transactionDetails.getConfirmString()));
		 ibtConfirm.setSourceApplication((int)cc.getChannelsourceapplication());
		 ibtConfirm.setChannelCode(cc.getChannelcode());
		 ibtConfirm.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		 //set biller code from sys parameters ibt
		 String ibtPartnerBillerCode = systemParametersService.getString(SystemParameterKeys.UANGKU_IBT_BILLER_CODE);
		 transactionDetails.setBillerCode(ibtPartnerBillerCode);
		 ibtConfirm.setUICategory(CmFinoFIX.TransactionUICategory_InterBank_Transfer);
		log.info("Handling Subscriber interbank confirmation WebAPI request");
		XMLResult result = new MoneyTransferXMLResult();
		//2FA
		ServiceChargeTxnLog sctlForMFA = sctlService.getByTransactionLogId(ibtConfirm.getParentTransactionID());
		if(mfaService.isMFATransaction(serviceName, transactionName, cc.getId().longValue())){
			if(transactionOtp == null || !(mfaService.isValidOTP(transactionOtp,sctlForMFA.getId().longValue(), ibtConfirm.getSourceMDN()))){
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidMFAOTP);
				return result;
			}
		}
		
		//For Integration Code
		MFSBillerPartner mfsBillerPartner = mfsBillerPartnerMapService.getByBillerCode(ibtConfirm.getBillerCode());
		if (mfsBillerPartner != null){
			ibtConfirm.setIntegrationCode(mfsBillerPartner.getIntegrationcode());
		}

		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_BillPay,ibtConfirm.DumpFields(),ibtConfirm.getParentTransactionID());
		ibtConfirm.setTransactionID(transactionsLog.getId().longValue());

		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMessage(ibtConfirm);
		result.setTransactionID(ibtConfirm.getTransactionID());
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(ibtConfirm.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+ibtConfirm.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		Pocket subPocket = pocketService.getDefaultPocket(sourceMDN, srcPocketCode);
		validationResult = transactionApiValidationService.validateSourcePocket(subPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(subPocket!=null? subPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		ibtConfirm.setSourceBankAccountNo(subPocket.getCardpan());
		
		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(subPocket);
		result.setPocketList(pocketList);
       
		Partner partner = billerService.getPartner(ibtPartnerBillerCode);

		SubscriberMdn partnerMDN = partner.getSubscriber().getSubscriberMdns().iterator().next();
		validationResult = transactionApiValidationService.validatePartnerMDN(partnerMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Destination partner has failed validations");
			validationResult = processValidationResultForPartner(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}


		ibtConfirm.setDestMDN(partnerMDN.getMdn());
		ibtConfirm.setSourcePocketID(subPocket.getId().longValue());
		ibtConfirm.setSourceApplication((int)cc.getChannelsourceapplication());
		ibtConfirm.setEmail(sourceMDN.getSubscriber().getEmail());
		
		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTxnLog sctl = transactionChargingService.getServiceChargeTransactionLog(ibtConfirm.getParentTransactionID(),ibtConfirm.getTransactionIdentifier());
			
		
		if (sctl != null) {
			ibtConfirm.setServiceChargeTransactionLogID(sctl.getId().longValue());
			BillPaymentsQuery bpquery = new BillPaymentsQuery();
			bpquery.setSctlID(sctl.getId().longValue());
			List<BillPayments> res = billPaymentsService.get(bpquery);
			if(res.size() > 0){
				ibtConfirm.setBenOpCode(res.get(0).getInfo1());
				ibtConfirm.setInvoiceNumber(res.get(0).getInvoicenumber());
				ibtConfirm.setDestinationBankAccountNo(res.get(0).getInvoicenumber());
				ibtConfirm.setIntegrationCode(res.get(0).getIntegrationcode());
				ibtConfirm.setPartnerBillerCode(res.get(0).getPartnerbillercode());
				Iterator<MFSBillerPartner> mfsBillers=partner.getMfsbillerPartnerMaps().iterator();
				while(mfsBillers.hasNext()){
					MFSBillerPartner mfsbiller = mfsBillers.next();
					if(mfsbiller.getMfsBiller().getMfsbillercode().equals(ibtConfirm.getBillerCode())){
						ibtConfirm.setBillerPartnerType(mfsbiller.getBillerpartnertype().intValue());
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
		PartnerServices partnerService = transactionChargingService.getPartnerService(partner.getId().longValue(), sctl.getServiceproviderid().longValue(), sctl.getServiceid().longValue());
		if (partnerService == null) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
			return result;
		}
		destPocket = partnerService.getPocketByDestpocketid();
		validationResult = transactionApiValidationService.validateSourcePocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
	
		ibtConfirm.setDestPocketID(destPocket.getId().longValue());

		CFIXMsg response = super.process(ibtConfirm);
		result.setMultixResponse(response);
		commodityTransferService.addCommodityTransferToResult(result);
		result.setSctlID(sctl.getId().longValue());
		
		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		
		BillPayments billPayments = null;
		BillPaymentsQuery query = new BillPaymentsQuery();
		query.setSctlID(sctl.getId().longValue());
		List<BillPayments> billLst = billPaymentsService.get(query);
		if(billLst != null && billLst.size() > 0){
			billPayments = billLst.get(0);
		}
		result.setResponseMessage(transactionResponse.getOperatorMsg());

		
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {
				transactionChargingService.confirmTheTransaction(sctl, ibtConfirm.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, ibtConfirm.getTransferID());
				result.setDebitAmount(sctl.getTransactionamount());
				result.setCreditAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
				result.setAdditionalInfo(transactionResponse.getAdditionalInfo());				
				if(billPayments != null && billPayments.getOperatorcharges() != null){
					result.setServiceCharge(sctl.getCalculatedcharge().add(billPayments.getOperatorcharges()));
				} else {
					result.setServiceCharge(sctl.getCalculatedcharge());
				}
				result.setNominalAmount(billPayments.getNominalamount());
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
}