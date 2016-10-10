package com.mfino.transactionapi.handlers.interswitch.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.IntegrationPartnerMap;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetTransactionStatus;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashinStatus;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.impl.IntegrationPartnerMappingServiceImpl;
import com.mfino.service.impl.SCTLServiceImpl;
import com.mfino.service.impl.TransactionChargingServiceImpl;
import com.mfino.service.impl.TransactionLogServiceImpl;
import com.mfino.transactionapi.handlers.interswitch.InterswitchCashinStatusHandler;
import com.mfino.transactionapi.service.TransactionApiValidationService;

@Service("InterswitchCashinStatusHandlerImpl")
public class InterswitchCashinStatusHandlerImpl extends FIXMessageHandler implements InterswitchCashinStatusHandler{
	private static Logger	log	= LoggerFactory.getLogger(InterswitchCashinStatusHandlerImpl.class);
	CMInterswitchCashinStatus cashinStatus;
	ChannelCode	          channel;
	CMGetTransactionStatus transactionStatus;


 	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

 	public InterswitchCashinStatusHandlerImpl(){
 		
 	}
 	
	public InterswitchCashinStatusHandlerImpl(CMInterswitchCashinStatus status, ChannelCode cc, String transactionIdentifier) {
		this.cashinStatus = status;
		this.channel = cc;
		this.transactionStatus = new CMGetTransactionStatus();
		//this transactionIdefier is set to be used later for in the backend
		this.cashinStatus.setTransactionIdentifier(transactionIdentifier);
		this.transactionStatus.setTransactionIdentifier(transactionIdentifier);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Result handle() {
		XMLResult statusResult=getStatus();
		return statusResult;
	}

	private XMLResult getStatus() {
		//need to create a resultxml object
		XMLResult result = new XMLResult();
		TransactionLogServiceImpl transactionLogServiceImpl = new TransactionLogServiceImpl();
		TransactionLog transactionsLog = transactionLogServiceImpl.saveTransactionsLog(CmFinoFIX.MessageType_InterswitchCashinStatus, cashinStatus.DumpFields());
		transactionStatus.setTransactionID(transactionsLog.getId().longValue());
		
		
		result.setSourceMessage(transactionStatus);
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());

		IntegrationPartnerMappingServiceImpl integrationPartnerMapping = new IntegrationPartnerMappingServiceImpl();
		IntegrationPartnerMap ipm  = integrationPartnerMapping.getByInstitutionID(cashinStatus.getInstitutionID());
		if (ipm == null) {
			log.info("Integration Partner Mapping is missing for InstitutionId : " + cashinStatus.getInstitutionID());
			result.setNotificationCode(CmFinoFIX.NotificationCode_PartnerNotFound);
			return result;
		}
		Partner cashinPartner =ipm.getPartner();

		Integer validationResult = transactionApiValidationService.validatePartnerByPartnerType(cashinPartner);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.info("Institution Partner is not Active");
			result.setNotificationCode(validationResult);
			return result;
		}

 		SubscriberMdn sourceMDN = cashinPartner.getSubscriber().getSubscriberMdns().iterator().next();

		transactionStatus.setSourceMDN(sourceMDN.getMdn());
		addCompanyANDLanguageToResult(sourceMDN, result);
		
		validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!validationResult.equals(CmFinoFIX.ResponseCode_Success)){
			result.setNotificationCode(validationResult);
			return result;
		}
		TransactionChargingServiceImpl transactionChargingService = new TransactionChargingServiceImpl();
		Transaction transaction = null;
		ServiceCharge serviceCharge = new ServiceCharge();
		serviceCharge.setSourceMDN(transactionStatus.getSourceMDN());
		serviceCharge.setDestMDN(null);
		serviceCharge.setChannelCodeId(channel.getId().longValue());
		serviceCharge.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
		serviceCharge.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_TRANSACTIONSTATUS);
		serviceCharge.setTransactionAmount(BigDecimal.ZERO);
		serviceCharge.setTransactionLogId(transactionsLog.getId().longValue());
		serviceCharge.setTransactionIdentifier(cashinStatus.getTransactionIdentifier());

		try{
			transaction =transactionChargingService.getCharge(serviceCharge);
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}

		ServiceChargeTxnLog sctl = transaction.getServiceChargeTransactionLog();
		transactionStatus.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		if(cashinStatus.getReferenceNumber() <= 0){
			log.info("Reference Number is not valid");
			result.setCode(CmFinoFIX.NotificationCode_InvalidPaymentLogID.toString());
			return result;
		}

		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		sctlQuery.setIntegrationTxnID(cashinStatus.getReferenceNumber());
		sctlQuery.setInfo1(cashinStatus.getCustReference());

		SCTLServiceImpl sctlService = new SCTLServiceImpl();
		List<ServiceChargeTxnLog> sctlList = sctlService.getByQuery(sctlQuery);
		ServiceChargeTxnLog oldSctl = null;
		if(!sctlList.isEmpty())
		{
			oldSctl = sctlList.get(0);
			if (sctl != null) {
				sctl.setCalculatedcharge(BigDecimal.ZERO);
				transactionChargingService.completeTheTransaction(sctl);
			}
		}
		if (oldSctl != null) {
			log.info("Returning the status of the requested transaction");
			result.setTransferID(oldSctl.getId().longValue());
			if (oldSctl.getStatus()==(CmFinoFIX.SCTLStatus_Confirmed) || oldSctl.getStatus()==(CmFinoFIX.SCTLStatus_Distribution_Completed)
					|| oldSctl.getStatus()==(CmFinoFIX.SCTLStatus_Distribution_Started))
				result.setCode(CmFinoFIX.NotificationCode_CashInToEMoneyCompletedToSender.toString());
			else
				result.setCode(CmFinoFIX.NotificationCode_Failure.toString());
			
			log.info("caashinStatus sctl status: " + oldSctl.getStatus());
			return result;			
		}
		return result;
	}	
}
