/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ChargetxnTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAutoReversal;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashOut;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashOutConfirm;
import com.mfino.fix.CmFinoFIX.CMJSBankTellerCashOutConfirm;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.i18n.MessageText;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.ChargeTxnCommodityTransferMapService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BankTellerCashOutApproveProcessor;

/**
 * 
 * @author Maruthi
 */
@Service("BankTellerCashOutApproveProcessorImpl")
public class BankTellerCashOutApproveProcessorImpl extends MultixCommunicationHandler implements BankTellerCashOutApproveProcessor{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private ChannelCode cc;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("ChargeTxnCommodityTransferMapServiceImpl")
	private ChargeTxnCommodityTransferMapService chargeTxnCommodityTransferMapService; 
	
	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionLogService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;

	@Override
	public CFIXMsg process(CFIXMsg msg) {

		CMJSBankTellerCashOutConfirm realMsg = (CMJSBankTellerCashOutConfirm) msg;
		CMJSError errorMsg = new CMJSError();

		MfinoUser user = userService.getCurrentUser();
		Set<Partner> partners = user.getPartners();
		if (partners == null || partners.isEmpty()) {
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}

		if (StringUtils.isBlank(realMsg.getMDN())||realMsg.getCommodityTransferID()==null
				||realMsg.getPocketID()==null||StringUtils.isBlank(realMsg.getPin())) {
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
		CommodityTransfer ct = commodityTransferService.getCommodityTransferById(realMsg.getCommodityTransferID());
		ChargeTxnCommodityTransferMapQuery query =new ChargeTxnCommodityTransferMapQuery();
		query.setCommodityTransferID(ct.getId().longValue());
		List<ChargetxnTransferMap> results =chargeTxnCommodityTransferMapService.getChargeTxnCommodityTransferMapByQuery(query);
		if(results==null||results.isEmpty()){
			log.info("SCTL not exist for commodityTransfet with id: "+ct.getId());
			errorMsg.setErrorDescription(MessageText._("Invalid Transaction"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		ServiceChargeTxnLog sctl = serviceChargeTransactionLogService.getById(results.get(0).getSctlid().longValue());
		cc = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_Web);
	       
		CMBankTellerCashOut cashOut = generateCashOutInquiry(ct,realMsg,tellerService,sctl);
		
		errorMsg= (CMJSError) handleRequestResponse(cashOut);

		if(CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode())){
        	CMBankTellerCashOutConfirm cashoutConfirm = generateCashOutConfirmation(errorMsg,ct,realMsg,tellerService,sctl);
        	errorMsg= (CMJSError) handleRequestResponse(cashoutConfirm);
        	if(CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode())){
        	 transactionChargingService.confirmTheTransaction(sctl);
        	}
        	else if(NOBACKEND_RESPONSE.equals(errorMsg.getErrorDescription())){
        		/*
        		 * Request to bank timed out, this should go to pending state.
        		 * */
        		//tcs.changeStatusToPendingResolved(sctl);
        		log.debug("BankTellerCashOUtApproveProcessor :: No Response from backend");
        	}
        	else{
        		//if it fails at backend, then change status to pending resolved
        		autoReverse(sctl, ct,  MessageText._("Cashout from Teller - Confirmation Failed"));
//        		tcs.changeStatusToPendingResolved(sctl);
        	}
         }          
		else{
			/*Inquiry failed*/
			autoReverse(sctl, ct,  MessageText._("Cashout from Teller - Inquiry Failed"));
//			tcs.changeStatusToPendingResolved(sctl);
		}
		
		return errorMsg;
	}

    // Do the Autoreversal if Transfer to Teller Bank pocket fails.
	private void autoReverse(ServiceChargeTxnLog sctl, CommodityTransfer ct,  String reverseReason) {
		log.info("constructing autoreversal object for Teller Cashout");
		CMAutoReversal reversal = new CMAutoReversal();
		reversal.setSourcePocketID(ct.getPocket().getId().longValue());
		reversal.setDestPocketID(ct.getDestpocketid().longValue());
		reversal.setServiceChargeTransactionLogID(sctl.getId().longValue());
		reversal.setAmount(ct.getAmount());
		reversal.setCharges(ct.getCharges());
		CMJSError emsg = (CMJSError) handleRequestResponse(reversal);
		log.info("Auto Reversal Staus for Teller Cashout: " + emsg.getErrorDescription());
		transactionChargingService.failTheTransaction(sctl, reverseReason);
	}
	
	private CMBankTellerCashOut generateCashOutInquiry(CommodityTransfer ct,CMJSBankTellerCashOutConfirm realMsg, PartnerServices tellerService, ServiceChargeTxnLog sctl) {
		CMBankTellerCashOut cashoutInquiry = new CMBankTellerCashOut();
		cashoutInquiry.setSourceMDN(realMsg.getMDN());
		cashoutInquiry.setDestMDN(realMsg.getMDN());
		cashoutInquiry.setSourcePocketID(realMsg.getPocketID());
		cashoutInquiry.setDestPocketID(tellerService.getPocketBySourcepocket().getId().longValue());
		cashoutInquiry.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_TELLER_CASH_OUT_TRANSFERTOBANK);
		cashoutInquiry.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
		cashoutInquiry.setSourceApplication((cc.getChannelsourceapplication()).intValue());
		cashoutInquiry.setAmount(ct.getAmount());
		cashoutInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());
		cashoutInquiry.setCharges(BigDecimal.ZERO);
		cashoutInquiry.setPin(realMsg.getPin());
		cashoutInquiry.setServletPath(CmFinoFIX.ServletPath_BankAccount);
		cashoutInquiry.setTransactionID(sctl.getTransactionid().longValue());
		cashoutInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());
		cashoutInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank);
		return cashoutInquiry;
	}
	
	
	private CMBankTellerCashOutConfirm generateCashOutConfirmation(CMJSError errorMsg, CommodityTransfer ct,CMJSBankTellerCashOutConfirm realMsg, PartnerServices tellerService, ServiceChargeTxnLog sctl) {
		CMBankTellerCashOutConfirm confirmation = new CMBankTellerCashOutConfirm();
		confirmation.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
		confirmation.setSourceApplication((cc.getChannelsourceapplication()).intValue());
		confirmation.setSourceMDN(realMsg.getMDN());
		confirmation.setDestMDN(realMsg.getMDN());
		confirmation.setSourcePocketID(realMsg.getPocketID());
		confirmation.setDestPocketID(tellerService.getPocketBySourcepocket().getId().longValue());
		confirmation.setServletPath(CmFinoFIX.ServletPath_BankAccount);
		confirmation.setTransactionID(errorMsg.getParentTransactionID());
		confirmation.setTransferID(errorMsg.getTransferID());
		confirmation.setServiceChargeTransactionLogID(sctl.getId().longValue());
		confirmation.setConfirmed(CmFinoFIX.Boolean_True);
		return confirmation;
	}

	

}
