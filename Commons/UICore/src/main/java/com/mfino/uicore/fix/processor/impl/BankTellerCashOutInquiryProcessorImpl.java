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
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChargetxnTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.MfinoUser;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.service.ChargeTxnCommodityTransferMapService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BankTellerCashOutInquiryProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CommodityTransferUpdateMessage;

/**
 * 
 * @author Maruthi
 */
@Service("BankTellerCashOutInquiryProcessorImpl")
public class BankTellerCashOutInquiryProcessorImpl extends
		BaseFixProcessor implements BankTellerCashOutInquiryProcessor{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("CommodityTransferUpdateMessageImpl")
	private CommodityTransferUpdateMessage commodityTransferUpdateMessage;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
	

	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("ChargeTxnCommodityTransferMapServiceImpl")
	private ChargeTxnCommodityTransferMapService chargeTxnCommodityTransferMapService;
	
	@Override
	public CFIXMsg process(CFIXMsg msg) throws Exception {

		CMJSCommodityTransfer realMsg = (CMJSCommodityTransfer) msg;
		CMJSError errorMsg = new CMJSError();

		MfinoUser user = userService.getCurrentUser();
		Set<Partner> partners = user.getPartners();
		if (partners == null || partners.isEmpty()) {
			errorMsg.setErrorDescription(MessageText._("You are not authorized to perform this operation"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}

		Partner partner = partners.iterator().next();
		Subscriber partnersub = partner.getSubscriber();
		SubscriberMdn partnerMDN = partnersub.getSubscriberMdns().iterator().next();
		realMsg.setDestMDN(partnerMDN.getMdn());
		
		if (StringUtils.isBlank(realMsg.getSourceMDN())) {
			log.info("SubscriberMDn is null");
			errorMsg.setErrorDescription(MessageText._("please enter MDN"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		if (realMsg.getServiceChargeTransactionLogID() == null) {
			log.info("Reference ID is null");
			errorMsg.setErrorDescription(MessageText._("Please enter Reference ID"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		realMsg.setSourceMDN(subscriberService.normalizeMDN(realMsg.getSourceMDN()));
		

		Long serviceId = transactionChargingService.getServiceId(ServiceAndTransactionConstants.SERVICE_TELLER);
		Long transactionTypeId = transactionChargingService.getTransactionTypeId(ServiceAndTransactionConstants.TRANSACTION_CASHOUT);
		ServiceChargeTransactionsLogQuery query = new ServiceChargeTransactionsLogQuery();
		query.setId(realMsg.getServiceChargeTransactionLogID());
		query.setDestMdn(realMsg.getDestMDN());
		query.setSourceMdn(realMsg.getSourceMDN());
		query.setServiceID(serviceId);
		query.setTransactionTypeID(transactionTypeId);
		List<ServiceChargeTxnLog> results = sctlService.getByQuery(query);
		
		CommodityTransfer ct = null;
		if (results != null && results.size() > 0) {
			ServiceChargeTxnLog sctl = results.get(0);
			if(sctl.getCommoditytransferid()!=null){
				ct = commodityTransferService.getCommodityTransferById(sctl.getCommoditytransferid().longValue());
				if(ct==null||(!((Long)ct.getTransferstatus()).equals(CmFinoFIX.TransactionsTransferStatus_Completed))){
					log.info("No Successful cashout Transaction Found for sctlID"+sctl.getId());
					errorMsg.setErrorDescription(MessageText._("No Successful Transaction Found"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					return errorMsg;
				}
			}
			
			CMJSError checkConfirmed =checkAlreadyConfirmed(sctl);
			ct = commodityTransferService.getCommodityTransferById(sctl.getCommoditytransferid().longValue());
			if(!CmFinoFIX.ErrorCode_NoError.equals(checkConfirmed.getErrorCode())){
				return checkConfirmed;	
			}
			if(!((Long)sctl.getStatus()).equals(CmFinoFIX.SCTLStatus_Processing)){
				errorMsg.setErrorDescription(MessageText._("Transaction status does not allow approval"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			realMsg.allocateEntries(1);			
			CMJSCommodityTransfer.CGEntries entry = new CMJSCommodityTransfer.CGEntries();
			commodityTransferUpdateMessage.updateMessage(ct, null, entry, realMsg);
			entry.setTransferStateText(CmFinoFIX.TransferStateValue_Complete);
			realMsg.getEntries()[0] = entry;
		}else{
			log.info("No cashout record found for sctlID"+realMsg.getServiceChargeTransactionLogID());
			errorMsg.setErrorDescription(MessageText._("No CashOut Transaction Found for given ReferenceID"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			return errorMsg;
		}
		return realMsg;
	}

	private CMJSError checkAlreadyConfirmed(ServiceChargeTxnLog sctl) {
		CMJSError error = new CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
		ChargeTxnCommodityTransferMapQuery query =new ChargeTxnCommodityTransferMapQuery();
		query.setSctlID(sctl.getId().longValue());
 		List<ChargetxnTransferMap> results =chargeTxnCommodityTransferMapService.getChargeTxnCommodityTransferMapByQuery(query);
		if(results==null||results.isEmpty()){
			error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			error.setErrorDescription("No Transaction found");
			return error;
		}
		Long ctId = sctl.getCommoditytransferid().longValue();
		for(ChargetxnTransferMap txnTransfer:results){
			if(txnTransfer.getCommoditytransferid().equals(ctId)){
				continue;
			}
			CommodityTransfer commodityTransfer = commodityTransferService.getCommodityTransferById(txnTransfer.getCommoditytransferid().longValue());
			if(commodityTransfer==null){
				commodityTransfer = commodityTransferService.getCommodityTransferById(txnTransfer.getCommoditytransferid().longValue());
			}
			if(ctId==null&&CmFinoFIX.TransactionUICategory_Teller_Cashout.equals(commodityTransfer.getUicategory())){
				ctId = commodityTransfer.getId().longValue();
				if(commodityTransfer instanceof CommodityTransfer){
					updateSctl(sctl.getId().longValue(), commodityTransfer);
					if(!CmFinoFIX.TransactionsTransferStatus_Completed.equals(commodityTransfer.getTransferstatus())){
						log.info("Cashout failed");
						error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
						error.setErrorDescription("No Successful CashOut Transaction found");
						return error;
					}
				}else if(commodityTransfer instanceof CommodityTransfer){
					log.info("Cashout pending");
					error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					error.setErrorDescription("No Successful CashOut Transaction found");
					return error;
				}
			}
			//change uicategory to teller cash out confirm
			if(CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank.equals(commodityTransfer.getUicategory())
					&&(commodityTransfer instanceof CommodityTransfer ||CmFinoFIX.TransferStatus_Completed.equals(commodityTransfer.getTransferstatus()))){
				error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				error.setErrorDescription("Transaction already Approved");
				return error;
			}
		}
		return error;
	}

	private void updateSctl(Long sctlId, CommodityTransfer commodityTransfer) {
		ServiceChargeTxnLog sctl = sctlService.getBySCTLID(sctlId);
		if(sctl.getCommoditytransferid()==null){

			transactionChargingService.addTransferID(sctl, commodityTransfer.getId().longValue());
		}
	}

}
