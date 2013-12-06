/**
 * 
 */
package com.mfino.transactionapi.handlers.payment.agent.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.Partner;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.BillPaymentsService;
import com.mfino.service.BillerService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.PendingCommodityTransferService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.payment.agent.AgentBillPayConfirmHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * Currently this handler handles bill pay for DSTV alone, need to refactor for any bill pay
 * 
 * @author Chaitanya
 * 
 */
@Service("AgentBillPayConfirmHandlerImpl")
public class AgentBillPayConfirmHandlerImpl extends FIXMessageHandler implements AgentBillPayConfirmHandler{

	private static Logger log	= LoggerFactory.getLogger(AgentBillPayConfirmHandlerImpl.class);
	
	@Autowired
	@Qualifier("BillerServiceImpl")
	private BillerService billerService;
	
	@Autowired
	@Qualifier("BillPaymentsServiceImpl")
	private BillPaymentsService billPaymentsService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("PendingCommodityTransferServiceImpl")
	private PendingCommodityTransferService pendingCommodityTransferService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	public Result handle(TransactionDetails transDetails) {
		CMBillPay	billPay = new CMBillPay();
		ChannelCode cc = transDetails.getCc();
		
		billPay.setSourceMDN(transDetails.getSourceMDN());
		billPay.setBillerCode(transDetails.getBillerCode());
		billPay.setInvoiceNumber(transDetails.getBillNum());
		billPay.setParentTransactionID(transDetails.getParentTxnId());
		billPay.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		billPay.setTransferID(transDetails.getTransferId());
		billPay.setConfirmed(Boolean.parseBoolean(transDetails.getConfirmString()));
		billPay.setSourceApplication(cc.getChannelSourceApplication());
		billPay.setChannelCode(cc.getChannelCode());
		billPay.setOnBeHalfOfMDN(transDetails.getOnBehalfOfMDN());
		billPay.setTransactionIdentifier(transDetails.getTransactionIdentifier());
		
		log.info("Handling Subscriber bill pay confirmation WebAPI request");
		XMLResult result = new MoneyTransferXMLResult();

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AgentDSTVPayment,billPay.DumpFields(),billPay.getParentTransactionID());
		billPay.setTransactionID(transactionsLog.getID());

		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMessage(billPay);
		result.setTransactionID(billPay.getTransactionID());

		PendingCommodityTransfer ct = pendingCommodityTransferService.getById(billPay.getTransferID());

		if(ct==null){
			log.info("Commodity Transfer record with transferid="+billPay.getTransferID()+" not found");
			result.setNotificationCode(CmFinoFIX.NotificationCode_TransferRecordNotFound);
			return result;
		}
		
		Pocket agentPocket = ct.getPocketBySourcePocketID();
		Pocket billerPocket= pocketService.getById(ct.getDestPocketID());
		
		log.info("source pocket id="+agentPocket.getID() +" Dest PocketID="+billerPocket.getID());
		

		SubscriberMDN smdn = subscriberMdnService.getByMDN(billPay.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateAgentMDN(smdn);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Subscriber with mdn : "+billPay.getSourceMDN()+" has failed agent validations");
			result.setNotificationCode(validationResult);
			return result;

		}

		Partner biller = billerService.getPartner(billPay.getBillerCode());

		if(biller == null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			return result;
		}

		List<Pocket> pocketList = new ArrayList<Pocket>();
		pocketList.add(agentPocket);
		result.setPocketList(pocketList);

		billPay.setDestMDN(ct.getDestMDN());
		billPay.setSourcePocketID(agentPocket.getID());
		billPay.setDestPocketID(billerPocket.getID());
		billPay.setSourceApplication(cc.getChannelSourceApplication());
		billPay.setEmail(smdn.getSubscriber().getEmail());
		billPay.setPartnerBillerCode(biller.getMFSBillerPartnerFromPartnerID().iterator().next().getPartnerBillerCode());

		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(billPay.getParentTransactionID(),billPay.getTransactionIdentifier());
		if (sctl != null) {
			billPay.setServiceChargeTransactionLogID(sctl.getID());
			
			BillPaymentsQuery bpquery = new BillPaymentsQuery();
			bpquery.setSctlID(sctl.getID());
			List<BillPayments> res = billPaymentsService.get(bpquery);
			if(res.size() > 0){
				billPay.setIntegrationCode(res.get(0).getIntegrationCode());
				billPay.setPartnerBillerCode(res.get(0).getPartnerBillerCode());
				Iterator<MFSBillerPartner> mfsBillers=biller.getMFSBillerPartnerFromPartnerID().iterator();
				while(mfsBillers.hasNext()){
					MFSBillerPartner mfsbiller = mfsBillers.next();
					if(mfsbiller.getMFSBiller().getMFSBillerCode().equals(billPay.getBillerCode())){
						billPay.setBillerPartnerType(mfsbiller.getBillerPartnerType());
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


		CFIXMsg response = super.process(billPay);
		result.setMultixResponse(response);
		commodityTransferService.addCommodityTransferToResult(result);
		result.setSctlID(sctl.getID());

		// Changing the Service_charge_transaction_log status based on the response from Core engine.
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		if (!("Your request is queued. Please check after sometime.".equals(transactionResponse.getMessage()))) {
			if (transactionResponse.isResult()) {
				transactionChargingService.confirmTheTransaction(sctl, billPay.getTransferID());
				commodityTransferService.addCommodityTransferToResult(result, billPay.getTransferID());
				result.setDebitAmount(sctl.getTransactionAmount());
				result.setCreditAmount(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
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
}