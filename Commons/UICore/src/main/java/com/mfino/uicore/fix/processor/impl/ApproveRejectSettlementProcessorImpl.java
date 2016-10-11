package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.ClosedAccountSettlementMDNQuery;
import com.mfino.dao.query.MoneyClearanceGravedQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CloseAcctSetlMdn;
import com.mfino.domain.MoneyClearanceGraved;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSApproveRejectSettlement;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.result.XMLResult;
import com.mfino.service.AuthorizationService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.ClosedAccountSettlementMDNService;
import com.mfino.service.MoneyClearanceGravedService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.UserService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.wallet.MoveBalanceConfirmHandler;
import com.mfino.transactionapi.handlers.wallet.MoveBalanceInquiryHandler;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.uicore.fix.processor.ApproveRejectSettlementProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

/**
 * @author Satya
 *
 */
@Service("ApproveRejectSettlementProcessorImpl")
public class ApproveRejectSettlementProcessorImpl extends BaseFixProcessor implements ApproveRejectSettlementProcessor{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	ChannelCode channelCode;

	@Autowired
	@Qualifier("MoveBalanceInquiryHandlerImpl")
	private MoveBalanceInquiryHandler moveBalanceInquiryHandler;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;
	
	@Autowired
	@Qualifier("MoveBalanceConfirmHandlerImpl")
	private MoveBalanceConfirmHandler moveBalanceConfirmHandler;
	
	@Autowired
	@Qualifier("ClosedAccountSettlementMDNServiceImpl")
	private ClosedAccountSettlementMDNService closedAccountSettlementMDNService;
	

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("MoneyClearanceGravedServiceImpl")
	private MoneyClearanceGravedService moneyClearanceGravedService;
	
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg){
		channelCode = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_BackEnd);
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		if (!authorizationService.isAuthorized(CmFinoFIX.Permission_Subscriber_Approve)) {
			log.info("You are not authorized to perform this operation");
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}

		CMJSApproveRejectSettlement realMsg = (CMJSApproveRejectSettlement) msg;
		log.info("Procesing the Closed Account Settlement Approve/Reject Request of" + realMsg.getSubscriberMDNID());
		if (realMsg.getSubscriberMDNID() == null) {
			log.info("subscriberMDNid is null");
			errorMsg.setErrorDescription(MessageText._("Invalid mdn status"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}

		Long subscriberMDNID = realMsg.getSubscriberMDNID();
		SubscriberMdn subscriberMDN = subscriberMdnService.getSubscriberMDNById(subscriberMDNID);

		if (null == subscriberMDN) {
			log.info("Invalid subscriberMDN" + realMsg.getSubscriberMDNID());
			errorMsg.setErrorDescription(MessageText._("Invalid MDNID"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		
		
		MoneyClearanceGravedQuery mcgQuery = new MoneyClearanceGravedQuery();
		mcgQuery.setMdnId(realMsg.getSubscriberMDNID());
		List<MoneyClearanceGraved> lst = moneyClearanceGravedService.getMoneyClearanceGravedByQuery(mcgQuery);
		
		
		Pocket srcSystemProviderPocket = getSrcSystemProvidePocket(systemParametersService.getLong(SystemParameterKeys.RETIRED_SUBSCRIBER_SYSTEM_COLLECTOR_POCKET));
		SubscriberMdn srcSystemProviderMDN=null;
		if(srcSystemProviderPocket != null){
			srcSystemProviderMDN = srcSystemProviderPocket.getSubscriberMdn();
		}
		else{
			log.info("Failed to move balance from system provider pocket as it is not configured");
			errorMsg.setErrorDescription(MessageText._("Invalid Source Pocket"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		
		ClosedAccountSettlementMDNQuery casmQuery = new ClosedAccountSettlementMDNQuery();
		casmQuery.setMdnId(realMsg.getSubscriberMDNID());
		List<CloseAcctSetlMdn> casmLst = closedAccountSettlementMDNService.getClosedAccountSettlementMDNByQuery(casmQuery);
		boolean toBankAccount = false;
		String settlementMDN = null;
		String settlementAccountNumber = null;
		CloseAcctSetlMdn casm;
		
		if(casmLst.size() > 0){
			casm = casmLst.get(0);
			toBankAccount = (casm.getTobankaccount() != 0);
			settlementMDN = casm.getSettlementmdn();
			settlementAccountNumber = casm.getSettlementaccountnumber();
			if(!settlementAccountNumber.isEmpty()){
				String accountNo = settlementAccountNumber;
				PocketQuery query = new PocketQuery();
				query.setCardPan(accountNo);
				List<Pocket> pockets = pocketService.get(query);
				if(CollectionUtils.isNotEmpty(pockets))
				{
					Pocket pocket = pockets.get(0);
					SubscriberMdn destMDN = pocket.getSubscriberMdn();
					settlementMDN = destMDN.getMdn();
				}
				else
				{
					settlementMDN = systemParametersService.getString(SystemParameterKeys.PLATFORM_DUMMY_SUBSCRIBER_MDN);
				}
			}
		}
		else{
			log.info("Settlement Details not configured");
			errorMsg.setErrorDescription(MessageText._("Invalid Settlement Details"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}		
		
		if (CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())) {
			
			boolean moneyMovedSuccessfully = false;
			
			for(MoneyClearanceGraved mcg:lst){
				TransactionDetails txnDetails = new TransactionDetails();
				txnDetails.setSourceMDN(srcSystemProviderMDN.getMdn());
				txnDetails.setSrcPocketId(srcSystemProviderPocket.getId().longValue());
				txnDetails.setDestMDN(settlementMDN);
				if(settlementAccountNumber != null && !settlementAccountNumber.isEmpty()){
					txnDetails.setDestinationBankAccountNo(settlementAccountNumber);
				}
				txnDetails.setSourcePIN("1234");
				txnDetails.setAmount(mcg.getAmount());
				txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_REFUND_INQUIRY);
				txnDetails.setSourcePocketCode(ApiConstants.POCKET_CODE_SVA);
				txnDetails.setChannelCode(channelCode.getChannelcode().toString());
				txnDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				txnDetails.setSourceMessage(ServiceAndTransactionConstants.TRANSACTION_REFUND_INQUIRY);
				if(toBankAccount){
					txnDetails.setDestPocketCode(ApiConstants.POCKET_CODE_BANK);
				}
				else{
					txnDetails.setDestPocketCode(ApiConstants.POCKET_CODE_SVA);
				}
				
				moneyMovedSuccessfully = moveSettlementMoney(txnDetails, toBankAccount,subscriberMDNID,settlementAccountNumber);
				
				if(moneyMovedSuccessfully){
					log.info("Successfully Moved Settlement amount of Retired Subscriber with subscriber ID --> " 
							+ realMsg.getSubscriberMDNID() + " to the settlement MDN/Account");							
				}
				else{
					log.info("Failed to move Settlement amount of Retired Subscriber with subscriber ID --> " 
							+ realMsg.getSubscriberMDNID() + " to the settlement MDN/Account");
					errorMsg.setErrorDescription(MessageText._("Settlement Failed"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					return errorMsg;
				}				
			}
			casm.setApprovalstate(((Integer)CmFinoFIX.ApprovalState_Approved).longValue());
			casm.setApprovedorrejectedby(userService.getCurrentUser().getUsername());
			casm.setApproveorrejectcomment(realMsg.getAdminComment());
			casm.setApproveorrejecttime(new Timestamp());
			closedAccountSettlementMDNService.saveClosedAccountSettlementMDN(casm);
			errorMsg.setErrorDescription(MessageText._("Successfully approved the subscriber for settlement"));

		} else if (CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())) {
			casm.setApprovalstate(((Integer)CmFinoFIX.ApprovalState_Rejected).longValue());
			casm.setApprovedorrejectedby(userService.getCurrentUser().getUsername());
			casm.setApproveorrejectcomment(realMsg.getAdminComment());
			casm.setApproveorrejecttime(new Timestamp());
			closedAccountSettlementMDNService.saveClosedAccountSettlementMDN(casm);
			errorMsg.setErrorDescription(MessageText._("Successfully rejected the subscriber for settlement"));
		} else {
			log.info("Invalid Mdn" + realMsg.getSubscriberMDNID());
			errorMsg.setErrorDescription(MessageText._("Invalid MDN"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		return errorMsg;
	}

	private boolean moveSettlementMoney(TransactionDetails txnDetails, boolean toBankAccount,Long subscriberMDNID,String refundAccountNumber) {
		XMLResult inquiryResult;
		XMLResult confirmResult;
		txnDetails.setCc(channelCode);
		inquiryResult = sendMoneyTransferInquiry(txnDetails,subscriberMDNID);
		
		if(!isMoneyTransferInquirySuccessfull(inquiryResult)){
			log.info("Inquiry for money transfer failed with notification code :" + inquiryResult.getNotificationCode()+" for retired subscriber with ID -->"+ subscriberMDNID );
			return false;
		}
		
		txnDetails.setConfirmString("true");		
		txnDetails.setTransferId(inquiryResult.getTransferID());
		txnDetails.setParentTxnId(inquiryResult.getParentTransactionID());
		txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_REFUND);
		confirmResult = sendMoneyTransferConfirm(txnDetails,subscriberMDNID);
		
		if(!isMoneyTransferConfirmSuccessfull(confirmResult)){
			log.info("Confirm for money transfer failed with notification code :" + confirmResult.getCode()+" for retired subscriber with ID -->"+ subscriberMDNID);
			return false;
		}		
		
		log.info("Money transfer successful with notification code :" + confirmResult.getCode()+" for retired subscriber with ID -->"+ subscriberMDNID );
		
		Long moneyClearanceID = updateMCGRecord(confirmResult,subscriberMDNID,refundAccountNumber);
		
		if(moneyClearanceID.equals(new Long(-1L))){
			log.info("Error in updating Money Clearance Object after transferring the amount to settlement MDN of retired subscriber with ID -->"+ subscriberMDNID );
			return false;
		}
		
		return true;		
	}

	private Long updateMCGRecord(XMLResult confirmResult,Long subscriberMDNID, String refundAccountNumber) {
		log.info("Trying to save money clerance object for Retired Subscriber with ID -->"+subscriberMDNID);
		MoneyClearanceGravedQuery query = new MoneyClearanceGravedQuery();
		MoneyClearanceGraved mcg;
		query.setMdnId(subscriberMDNID);
		List <MoneyClearanceGraved> lst = moneyClearanceGravedService.getMoneyClearanceGravedByQuery(query);
		if(lst.size() > 0){
			mcg = lst.get(0);
		}else{
			mcg = new MoneyClearanceGraved();
		}
		
		ServiceChargeTxnLog sctl = new ServiceChargeTxnLog();
		if(confirmResult != null){
			log.info("sctl ID from Confirm --> " + confirmResult.getSctlID() + " for Retired Subscriber with ID --> " + subscriberMDNID);
			sctl = sctlService.getBySCTLID(confirmResult.getSctlID());
			SubscriberMdn subscriberMDN = subscriberMdnService.getSubscriberMDNById(subscriberMDNID);
			SubscriberMdn destMDN = subscriberMdnService.getByMDN(confirmResult.getDestinationMDN());
			
			mcg.setSubscriberMdnByMdnid(subscriberMDN);
			mcg.setServiceChargeTxnLogByRefundsctlid(sctl);
			mcg.setMcstatus(CmFinoFIX.MCStatus_REFUNDED);
			mcg.setAmount(confirmResult.getCreditAmount());
			mcg.setSubscriberMdnByRefundmdnid(destMDN);
			mcg.setRefundaccountnumber(refundAccountNumber);
			mcg.setPocketByRefundpocketid(confirmResult.getSourcePocket());
			moneyClearanceGravedService.saveMoneyClearanceGraved(mcg);
			log.info("Successfully saved save money clerance object for Retired Subscriber with ID -->"+subscriberMDNID);
			return mcg.getId().longValue();
		}
		return new Long(-1L);
			
	}

	private XMLResult sendMoneyTransferInquiry(TransactionDetails txnDetails,Long subscriberMDNID) {
		log.info("Sending Inquiry for Settlement of Retired subscriber with ID -->" + subscriberMDNID);
		XMLResult xmlResult = null;
		txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_REFUND_INQUIRY);
	
		xmlResult = (XMLResult) moveBalanceInquiryHandler.handle(txnDetails);
		
		log.info("Inquiry Response for Retired Subscriber with ID -->:"+ subscriberMDNID +"is: "+ xmlResult);
		return xmlResult;		
	}
	
	private XMLResult sendMoneyTransferConfirm(TransactionDetails txnDetails,Long subscriberMDNID) {
		log.info("Sending TransferConfirm for Settlement of Retired subscriber with ID -->" + subscriberMDNID);
		XMLResult xmlResult = null;
		xmlResult = (XMLResult) moveBalanceConfirmHandler.handle(txnDetails);
		log.info("TransferConfirm return code for Settlement of Retired subscriber with ID --> "+ subscriberMDNID + "is: "+ xmlResult);
		return xmlResult;			
	}

	private Pocket getSrcSystemProvidePocket(long pocketID) {
		Pocket srcPocket = pocketService.getById(pocketID);
		return srcPocket;
	}

		
	private boolean isMoneyTransferInquirySuccessfull(XMLResult inquiryResult){
		if (inquiryResult != null) {		
			if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(inquiryResult.getCode())) {
				return true;
			}
		}
		return false;	
	}
	private boolean isMoneyTransferConfirmSuccessfull(XMLResult confirmResult) {
		if (confirmResult != null) {
			if (CmFinoFIX.NotificationCode_BankAccountToBankAccountCompletedToSenderMDN
					.toString().equals(confirmResult.getCode())
					|| CmFinoFIX.NotificationCode_EMoneytoEMoneyCompleteToSender
							.toString().equals(confirmResult.getCode())
							|| CmFinoFIX.NotificationCode_EMoneyToBankAccountCompletedToSender
							.toString().equals(confirmResult.getCode())) {
				return true;
			}
		}
		return false;
	}
	
}
