/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;

import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.domain.User;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiryForNonRegistered;
import com.mfino.fix.CmFinoFIX.CMJSCashOutUnregisteredInquiry;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.result.XMLResult;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BankTellerUnregisteredCashOutInquiryProcessor;
import com.mfino.util.MfinoUtil;
import com.mfino.validators.PINValidator;

/**
 * 
 * @author Maruthi
 */
@Service("BankTellerUnregisteredCashOutInquiryProcessorImpl")
public class BankTellerUnregisteredCashOutInquiryProcessorImpl extends MultixCommunicationHandler implements BankTellerUnregisteredCashOutInquiryProcessor{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
    private ChannelCodeService channelCodeService;

	@Autowired
	@Qualifier("UnRegisteredTxnInfoServiceImpl")
	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Override
	public CFIXMsg process(CFIXMsg msg) {
		CMJSCashOutUnregisteredInquiry realMsg = (CMJSCashOutUnregisteredInquiry) msg;
		CMJSError errorMsg = new CMJSError();

		User user = userService.getCurrentUser();
		Set<Partner> partners = user.getPartnerFromUserID();
		if (partners == null || partners.isEmpty()) {
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}

		Partner partner = partners.iterator().next();
		Subscriber partnersub = partner.getSubscriber();
		SubscriberMDN partnerMDN = partnersub.getSubscriberMDNFromSubscriberID().iterator().next();
		SubscriberMDN subscriberMDN = null;
		if(!CmFinoFIX.MDNStatus_Active.equals(partnerMDN.getStatus())){
			log.info("PartnerMdn Status is not active");
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		if (realMsg.getOriginalReferenceID() == null) {
			log.info("TransferID is null");
			errorMsg.setErrorDescription(MessageText._("please enter TransferID"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		if (StringUtils.isBlank(realMsg.getPin())) {
			log.info("Pin is blank");
			errorMsg.setErrorDescription(MessageText._("please enter Pin"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		if (StringUtils.isBlank(realMsg.getDigestedPIN())) {
			log.info("secretecode is blank");
			errorMsg.setErrorDescription(MessageText._("please enter SecreteCode"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		if (StringUtils.isBlank(realMsg.getSourceMDN())) {
			log.info("SubscriberMDn is null");
			errorMsg.setErrorDescription(MessageText._("please enter MDN"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}else{
			realMsg.setSourceMDN(subscriberService.normalizeMDN(realMsg.getSourceMDN()));
			subscriberMDN=subscriberMdnService.getByMDN(realMsg.getSourceMDN());
			if(subscriberMDN==null){
				log.info("SubscriberMDN not exist");
				errorMsg.setErrorDescription(MessageText._("MDN not exist"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}else if(!CmFinoFIX.MDNStatus_NotRegistered.equals(subscriberMDN.getStatus())){
				log.info("SubscriberMDN registered ");
				errorMsg.setErrorDescription(MessageText._("MDN already registered "));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
		}
		XMLResult result = new XMLResult();
		PINValidator pinValidator = new PINValidator(partnerMDN.getMDN(), realMsg.getPin(),result);
		if(!CmFinoFIX.ResponseCode_Success.equals(pinValidator.validate())){
			log.info("Invalid Partner Pin counts left "+result.getNumberOfTriesLeft());
			errorMsg.setErrorDescription(MessageText._("Invalid Partner Pin counts left "+result.getNumberOfTriesLeft()));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		realMsg.setDestMDN(partnerMDN.getMDN());
		UnRegisteredTxnInfoQuery query = new UnRegisteredTxnInfoQuery();
		query.setTransferSctlId(realMsg.getOriginalReferenceID());	
		query.setSubscriberMDNID(subscriberMDN.getID());		
		List<UnregisteredTxnInfo> unRegisteredTxnInfo = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(query);
		if(unRegisteredTxnInfo==null||unRegisteredTxnInfo.isEmpty()){
			log.info("unregistered transaction info not exist with sctlid"+realMsg.getOriginalReferenceID());
			errorMsg.setErrorDescription(MessageText._("Transfer Record not found with given TransferId"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;	
		}
		UnregisteredTxnInfo txnInfo = unRegisteredTxnInfo.get(0);
		if(!(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED.equals(txnInfo.getUnRegisteredTxnStatus())
				||CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED.equals(txnInfo.getUnRegisteredTxnStatus()))){
			log.info("unregistered transaction info status "+txnInfo.getUnRegisteredTxnStatus());
			errorMsg.setErrorDescription(MessageText._("Transfer Record Status does not allow Cashout"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}else{
			String code = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), realMsg.getDigestedPIN());
			if(!txnInfo.getDigestedPIN().equals(code)){
				log.info("Invalid secrete code");
				errorMsg.setErrorDescription(MessageText._("Invalid secrete code"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			errorMsg = handleCashOutInquiry(realMsg,txnInfo,partnerMDN,subscriberMDN,partner);
			if(CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode())){
				realMsg.setTransferID(errorMsg.getTransferID());
				realMsg.setParentTransactionID(errorMsg.getParentTransactionID());
				realMsg.setsuccess(true);
			}else{
				return errorMsg;
			}
		}
		
		return realMsg;
	}
	
 	private CMJSError handleCashOutInquiry(CMJSCashOutUnregisteredInquiry realMsg,
			UnregisteredTxnInfo txnInfo, SubscriberMDN partnerMDN, SubscriberMDN destMDn, Partner partner) {
		CMJSError errorMsg = new CMJSError();
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		TransactionsLog transactionsLog = new TransactionsLog();

        transactionsLog.setMessageCode(CmFinoFIX.MsgType_JSCashOutUnregisteredInquiry);
        transactionsLog.setMessageData(realMsg.DumpFields());
        transactionsLog.setTransactionTime(new Timestamp(new Date()));
        transactionLogService.save(transactionsLog); 
        
        ChannelCode cc = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_Web);
       
        Pocket subPocket = subscriberService.getDefaultPocket(destMDn.getID(),systemParametersService.getLong(SystemParameterKeys.POCKET_TEMPLATE_UNREGISTERED));		
		if(subPocket==null||!subPocket.getStatus().equals(CmFinoFIX.PocketStatus_OneTimeActive)){
			log.info("subscriber pocket Null");
			errorMsg.setErrorDescription(MessageText._(" vaild Subscriber pocket not found "));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		

		CommodityTransfer ct = commodityTransferService.getCommodityTransferById(txnInfo.getTransferCTId());
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getID());
		sc.setDestMDN(partnerMDN.getMDN());
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED);
		sc.setSourceMDN(destMDn.getMDN());
		sc.setTransactionAmount(ct.getAmount());
		sc.setMfsBillerCode(partner.getPartnerCode());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
		sc.setTransactionLogId(transactionsLog.getID());
		Pocket partnerPocket = null;
		Pocket partnerbankPocket;
		
		try {
			long servicePartnerId = transactionChargingService.getServiceProviderId(null);
			long serviceId = transactionChargingService.getServiceId(sc.getServiceName());
			PartnerServices partnerService = transactionChargingService.getPartnerService(partner.getID(), servicePartnerId, serviceId);
			if (partnerService == null) {
				log.info("Partner service Null");
				errorMsg.setErrorDescription(MessageText._("You are not registered for this service"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}else{
			partnerPocket = partnerService.getPocketByDestPocketID();
			partnerbankPocket = partnerService.getPocketBySourcePocket();
			
			if(partnerbankPocket==null||(!partnerbankPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active))){
				log.info("Partner bank Pocket Not found");
				errorMsg.setErrorDescription(MessageText._(" valid bank Pocket not found"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}			
			if(partnerPocket==null){
				log.info("Partner Pocket Not found");
				errorMsg.setErrorDescription(MessageText._("Emoney Pocket not found"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			if (!partnerPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)) {
				log.info("Partner Pocket Not Active");
				errorMsg.setErrorDescription(MessageText._("Emoney Pocket not Active"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
		}
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting Source Pocket",e);
			errorMsg.setErrorDescription(MessageText._("Service not available"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		Transaction transaction=null;
		
		try{
			transaction =transactionChargingService.getCharge(sc);
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			errorMsg.setErrorDescription(MessageText._("Service not available"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			errorMsg.setErrorDescription(MessageText._("Invalid Charge definition"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;			
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();
		realMsg.setServiceChargeTransactionLogID(sctl.getID());
		txnInfo.setCashoutSCTLId(sctl.getID());
		txnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_REQUESTED);
		unRegisteredTxnInfoService.save(txnInfo);		
    	
    	CMCashOutInquiryForNonRegistered unregisteredSubscriberCashOutInquiry= new CMCashOutInquiryForNonRegistered();
		unregisteredSubscriberCashOutInquiry = new CMCashOutInquiryForNonRegistered();
		unregisteredSubscriberCashOutInquiry.setSourceMDN(destMDn.getMDN());
		unregisteredSubscriberCashOutInquiry.setDestMDN(partnerMDN.getMDN());
		unregisteredSubscriberCashOutInquiry.setPin(realMsg.getPin());
		unregisteredSubscriberCashOutInquiry.setSourceApplication(cc.getChannelSourceApplication());
		unregisteredSubscriberCashOutInquiry.setChannelCode(cc.getChannelCode());
		unregisteredSubscriberCashOutInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		unregisteredSubscriberCashOutInquiry.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_TELLER_CASH_OUT);
		unregisteredSubscriberCashOutInquiry.setTransactionID(transactionsLog.getID());
		unregisteredSubscriberCashOutInquiry.setPartnerCode(partner.getPartnerCode());
		unregisteredSubscriberCashOutInquiry.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
		unregisteredSubscriberCashOutInquiry.setSourcePocketID(subPocket.getID());
		unregisteredSubscriberCashOutInquiry.setDestPocketID(partnerPocket.getID());
		unregisteredSubscriberCashOutInquiry.setServiceChargeTransactionLogID(sctl.getID());
		unregisteredSubscriberCashOutInquiry.setAmount(transaction.getAmountToCredit());
		unregisteredSubscriberCashOutInquiry.setCharges(transaction.getAmountTowardsCharges());
		unregisteredSubscriberCashOutInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Cashout_To_UnRegistered);
		realMsg.setSourcePocketID(subPocket.getID());
		realMsg.setDestPocketID(partnerPocket.getID());
		realMsg.setCharges(transaction.getAmountTowardsCharges());
		realMsg.setAmount(transaction.getAmountToCredit());
		errorMsg= (CMJSError) handleRequestResponse(unregisteredSubscriberCashOutInquiry);
		if(!CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode())){
			txnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED);
			if(errorMsg.getTransferID()!=null){
				txnInfo.setCashoutCTId(errorMsg.getTransferID());
			}
			unRegisteredTxnInfoService.save(txnInfo);	
	    }else{
		txnInfo.setCashoutCTId(errorMsg.getTransferID());
		unRegisteredTxnInfoService.save(txnInfo);	
	    }
		return errorMsg;
	}

	
}
