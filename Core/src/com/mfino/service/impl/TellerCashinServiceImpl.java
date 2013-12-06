package com.mfino.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashIn;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashInConfirm;
import com.mfino.service.SubscriberService;
import com.mfino.service.TellerCashinService;
import com.mfino.service.TransactionChargingService;
import com.mfino.validators.DestMDNValidator;
import com.mfino.validators.PartnerValidator;
import com.mfino.validators.PocketLimitsValidator;
import com.mfino.validators.Validator;

@Service("TellerCashinServiceImpl")
public class TellerCashinServiceImpl implements TellerCashinService{
	private Logger log = LoggerFactory.getLogger(TellerCashinServiceImpl.class);
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	
	/**
	 * Validates tellerCashinInquiry details by validating source mdn destination MDN and also 
	 * performs null checks on source pocket and teller pockets
	 * @param tellerCashinInquiry
	 * @param cc
	 * @return
	 */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer processInquiry(CMBankTellerCashIn tellerCashinInquiry,ChannelCode cc){

		PartnerValidator tellerMdnValidator = new PartnerValidator();
		tellerMdnValidator.setMdn(tellerCashinInquiry.getSourceMDN());
		
		Validator validator = new Validator();
		validator.addValidator(tellerMdnValidator);
		Integer validationResult= validator.validateAll();
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			return validationResult;
		}

		DestMDNValidator destMdnValidator = new DestMDNValidator(tellerCashinInquiry.getDestMDN());
		validationResult= destMdnValidator.validate();
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			return validationResult;
		}
		SubscriberMDN destinationMDN = destMdnValidator.getSubscriberMDN();
		
		Pocket subPocket = subscriberService.getDefaultPocket(destinationMDN.getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
		if(subPocket==null){
			return CmFinoFIX.NotificationCode_DestinationEMoneyPocketNotFound;
		} 
		else if (CmFinoFIX.PocketStatus_Active.intValue() != subPocket.getStatus().intValue()) {
			return CmFinoFIX.NotificationCode_MoneyPocketNotActive;
		}
		else {
			PocketLimitsValidator plValidator = new PocketLimitsValidator(tellerCashinInquiry.getAmount(), subPocket, false);
			validationResult = plValidator.validate();
			if (CmFinoFIX.ResponseCode_Success.intValue() != validationResult) {
				return validationResult;
			}
		}


		Transaction transDetails = null;
		ServiceCharge sc=new ServiceCharge();
		sc.setChannelCodeId(cc.getID());
		sc.setDestMDN(destinationMDN.getMDN());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CASHIN);
		sc.setSourceMDN(tellerMdnValidator.getSubscriberMDN().getMDN());
		sc.setTransactionAmount(tellerCashinInquiry.getAmount());
		sc.setTransactionLogId(tellerCashinInquiry.getTransactionID());

		Pocket tellerPocket;
		Pocket srcEmoneyPocket;
		try {
			PartnerServices partnerService = transactionChargingService.getPartnerService(sc);
			if (partnerService == null) {
				return CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner;
			}
			srcEmoneyPocket = partnerService.getPocketByDestPocketID();
			//SubscriberService.getDefaultPocket(tellerMdnValidator.getSubscriberMDN().getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
			tellerPocket =partnerService.getPocketBySourcePocket();
			//SubscriberService.getDefaultPocket(tellerMdnValidator.getSubscriberMDN().getID(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
			 
			if(tellerPocket==null||srcEmoneyPocket==null){
				return CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound;
			}
			if (!tellerPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)) {
				return CmFinoFIX.NotificationCode_MoneyPocketNotActive;
			}
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting Source Pocket",e);
			return CmFinoFIX.NotificationCode_ServiceNotAvailable;
		}

		try{
			transDetails =transactionChargingService.getCharge(sc);
			tellerCashinInquiry.setAmount(transDetails.getAmountToCredit());
			tellerCashinInquiry.setCharges(transDetails.getAmountTowardsCharges());
			tellerCashinInquiry.setSourcePocketID(tellerPocket.getID());
			tellerCashinInquiry.setDestPocketID(srcEmoneyPocket.getID());
		    tellerCashinInquiry.setEndDestPocketID(subPocket.getID());		      
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			return CmFinoFIX.NotificationCode_ServiceNotAvailable;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			return CmFinoFIX.NotificationCode_InvalidChargeDefinitionException;
		}
		
		ServiceChargeTransactionLog sctl = transDetails.getServiceChargeTransactionLog();
		tellerCashinInquiry.setServiceChargeTransactionLogID(sctl.getID());
		return CmFinoFIX.ResponseCode_Success;
		
	}
	
	/**
	 * Contains validations for subscriber mdn and teller mdn also validates pockets 
	 * and changes the status of sctl to processing if its in Inquiry state
	 * @param tellercashinconfirm
	 * @return status
	 */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer processConfirmation(CMBankTellerCashInConfirm tellercashinconfirm){
		PartnerValidator tellerMdnValidator = new PartnerValidator();
		tellerMdnValidator.setMdn(tellercashinconfirm.getSourceMDN());
	
		Validator validator = new Validator();
		validator.addValidator(tellerMdnValidator);	
		Integer validationResult= validator.validateAll();
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			return validationResult;
		}

		Partner teller = tellerMdnValidator.getPartner();
		SubscriberMDNDAO subMdndao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN tellerMDN=tellerMdnValidator.getSubscriberMDN();
		SubscriberMDN subscriberMDN = subMdndao.getByMDN(tellercashinconfirm.getDestMDN());
		if(tellerMDN==null){
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		if(subscriberMDN==null){
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		
		Pocket subPocket = subscriberService.getDefaultPocket(subscriberMDN.getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
		if(subPocket==null){
		return CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound;
		}

		// Changing the Service_charge_transaction_log status based on the response from Core engine.

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(tellercashinconfirm.getParentTransactionID());
		if (sctl != null) {
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
				transactionChargingService.chnageStatusToProcessing(sctl);
			} else {
				return CmFinoFIX.NotificationCode_TransferRecordChangedStatus;
			}

		} else {
			return CmFinoFIX.NotificationCode_TransferRecordNotFound;
		}
		
		Pocket tellerPocket;
		Pocket srcEmoneyPocket;
		PartnerServices partnerService = transactionChargingService.getPartnerService(teller.getID(), sctl.getServiceProviderID(), sctl.getServiceID());
		if (partnerService == null) {
			return CmFinoFIX.NotificationCode_ServiceNOTAvailableForPartner;
		}
		srcEmoneyPocket = partnerService.getPocketByDestPocketID();
		//SubscriberService.getDefaultPocket(tellerMdnValidator.getSubscriberMDN().getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
		tellerPocket =partnerService.getPocketBySourcePocket();
		//SubscriberService.getDefaultPocket(tellerMdnValidator.getSubscriberMDN().getID(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
		
		if(tellerPocket==null||srcEmoneyPocket==null){
			return CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound;
			}
		if (!tellerPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)) {
			return CmFinoFIX.NotificationCode_MoneyPocketNotActive;
			}
		tellercashinconfirm.setServiceChargeTransactionLogID(sctl.getID());
		tellercashinconfirm.setSourcePocketID(tellerPocket.getID());
		tellercashinconfirm.setDestPocketID(srcEmoneyPocket.getID()); 
		tellercashinconfirm.setEndDestPocketID(subPocket.getID());
		return CmFinoFIX.ResponseCode_Success;
	}
	
}
