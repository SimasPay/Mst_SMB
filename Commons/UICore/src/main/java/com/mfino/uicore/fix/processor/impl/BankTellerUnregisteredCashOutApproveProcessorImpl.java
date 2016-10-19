/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.domain.MfinoUser;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashOutForNonRegistered;
import com.mfino.fix.CmFinoFIX.CMJSBankTellerCashOutConfirm;
import com.mfino.fix.CmFinoFIX.CMJSCashOutUnregisteredConfirm;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.i18n.MessageText;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.PendingCommodityTransferService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BankTellerCashOutApproveProcessor;
import com.mfino.uicore.fix.processor.BankTellerUnregisteredCashOutApproveProcessor;

/**
 * 
 * @author Maruthi
 */
@Service("BankTellerUnregisteredCashOutApproveProcessorImpl")
public class BankTellerUnregisteredCashOutApproveProcessorImpl extends MultixCommunicationHandler implements BankTellerUnregisteredCashOutApproveProcessor{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private ChannelCode cc;
	
	@Autowired
	@Qualifier("BankTellerCashOutApproveProcessorImpl")
	private BankTellerCashOutApproveProcessor bankTellerCashOutApproveProcessor;
	 
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("PendingCommodityTransferServiceImpl")
	private PendingCommodityTransferService pendingCommodityTransferService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionLogService;
	
	@Autowired
	@Qualifier("UnRegisteredTxnInfoServiceImpl")
	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;

	@Override
	public CFIXMsg process(CFIXMsg msg) throws Exception{

		CMJSCashOutUnregisteredConfirm realMsg = (CMJSCashOutUnregisteredConfirm) msg;
		CMJSError errorMsg = new CMJSError();

		MfinoUser user = userService.getCurrentUser();
		Set<Partner> partners = user.getPartners();
		if (partners == null || partners.isEmpty()) {
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}

		if (StringUtils.isBlank(realMsg.getDestMDN())||realMsg.getTransferID()==null) {
			log.info("Invalid confirmation request");
			errorMsg.setErrorDescription(MessageText._("Could not process your request try again"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		Set<PartnerServices> ps = partners.iterator().next().getPartnerServicesesForPartnerid();
		PartnerServices tellerService = null;
		for(PartnerServices partnerservice:ps){
			if(partnerservice.getService().getServicename().equals(ServiceAndTransactionConstants.SERVICE_TELLER)
					&& (partnerservice.getStatus()).equals(CmFinoFIX.PartnerServiceStatus_Active)){
				tellerService = partnerservice;
				break;
			}
		}
		if(tellerService==null){
			log.info("Service not exist for partner");
			errorMsg.setErrorDescription(MessageText._("Service not exist for partner"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		PendingCommodityTransfer pct = pendingCommodityTransferService.getById(realMsg.getTransferID());
		if(pct==null||!CmFinoFIX.TransferStatus_ConfirmationPromptSentToSubscriber.equals(pct.getTransferstatus())){
			log.info("Invalid pct status"+pct.getTransferstatus());
			errorMsg.setErrorDescription(MessageText._("Invalid Transaction"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		cc = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_Web);
		
		CMCashOutForNonRegistered	unregisteredsubscribercashoutconfirm= new CMCashOutForNonRegistered();
		unregisteredsubscribercashoutconfirm.setSourceMDN(realMsg.getSourceMDN());
		unregisteredsubscribercashoutconfirm.setDestMDN(realMsg.getDestMDN());
		unregisteredsubscribercashoutconfirm.setParentTransactionID(realMsg.getParentTransactionID());
		unregisteredsubscribercashoutconfirm.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		unregisteredsubscribercashoutconfirm.setTransferID(pct.getId().longValue());
		unregisteredsubscribercashoutconfirm.setConfirmed(realMsg.getConfirmed());
		unregisteredsubscribercashoutconfirm.setSourceApplication(((Long)cc.getChannelsourceapplication()).intValue());
		unregisteredsubscribercashoutconfirm.setChannelCode(cc.getChannelcode());
		unregisteredsubscribercashoutconfirm.setServiceChargeTransactionLogID(realMsg.getServiceChargeTransactionLogID());
		unregisteredsubscribercashoutconfirm.setDestPocketID(realMsg.getDestPocketID());
		unregisteredsubscribercashoutconfirm.setSourcePocketID(realMsg.getSourcePocketID());
		unregisteredsubscribercashoutconfirm.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
		unregisteredsubscribercashoutconfirm.setPartnerCode(partners.iterator().next().getPartnercode());

		ServiceChargeTxnLog sctl = serviceChargeTransactionLogService.getById(realMsg.getServiceChargeTransactionLogID());
		transactionChargingService.chnageStatusToProcessing(sctl);
		
		UnRegisteredTxnInfoQuery query = new UnRegisteredTxnInfoQuery();
		query.setCashoutSCTLId(realMsg.getServiceChargeTransactionLogID());	
		List<UnregisteredTxnInfo> unRegisteredTxnInfo = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(query);
		if(unRegisteredTxnInfo==null||unRegisteredTxnInfo.isEmpty()){
			log.info("unregistered transaction info not exist with Cash out sctlid"+realMsg.getServiceChargeTransactionLogID());
			errorMsg.setErrorDescription(MessageText._("Transfer Record not found with given TransferId"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;	
		}
				
		errorMsg= (CMJSError) handleRequestResponse(unregisteredsubscribercashoutconfirm);
		String finalNotification =" Transaction ID:"+sctl.getId()+".You have successfully Cashed-Out to UnregisteredMDN "+realMsg.getSourceMDN()+" with "+sctl.getTransactionamount()+"\n"+"    Your EmoneyToBank Transfer Result: ";
		if(CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode())){
        	Long transferID= errorMsg.getTransferID();
        	errorMsg=(CMJSError) transferToBankAccount(realMsg,errorMsg.getTransferID());
        	if(CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode())){
        		finalNotification = finalNotification+"Successful";
        		transactionChargingService.confirmTheTransaction(sctl, transferID);            	
        	} else if(NOBACKEND_RESPONSE.equals(errorMsg.getErrorDescription())){
        		finalNotification = finalNotification +"Pending";
        		transactionChargingService.addTransferID(sctl, transferID);
        	}else{
        		finalNotification = finalNotification +"Failed";
        		transactionChargingService.addTransferID(sctl, transferID);
        	}
        	errorMsg.setErrorDescription(finalNotification);
		}
		 
		return errorMsg;
	}

	private CFIXMsg transferToBankAccount(CMJSCashOutUnregisteredConfirm realMsg, Long ctId) throws Exception {
		CMJSBankTellerCashOutConfirm transferToBank = new CMJSBankTellerCashOutConfirm();
		transferToBank.setMDN(realMsg.getDestMDN());
		transferToBank.setCommodityTransferID(ctId);
		transferToBank.setPin(realMsg.getPin());
		transferToBank.setPocketID(realMsg.getDestPocketID());
		return bankTellerCashOutApproveProcessor.process(transferToBank);
	}
}
