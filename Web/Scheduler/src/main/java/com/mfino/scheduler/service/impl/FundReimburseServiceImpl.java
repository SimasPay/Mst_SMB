/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.query.ChargeDefinitionQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.hibernate.Timestamp;
import com.mfino.scheduler.service.FundReimburseService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.ChargeDefinitionService;
import com.mfino.service.MfinoServiceProviderCoreService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionsLogCoreService;
import com.mfino.transactionapi.handlers.money.FundReimberseHandler;

/**
 * @author Maruthi
 *
 */
@Service("FundReimburseServiceImpl")
public class FundReimburseServiceImpl  implements FundReimburseService {
	private static Logger log = LoggerFactory.getLogger(FundReimburseServiceImpl.class);
	
	@Autowired
	@Qualifier("FundReimberseHandlerImpl")
	private FundReimberseHandler fundReimberseHandler;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService ;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	@Autowired
	@Qualifier("ChargeDefinitionServiceImpl")
	private ChargeDefinitionService chargeDefinitionService;
	
	@Autowired
	@Qualifier("TransactionsLogCoreServiceImpl")
	private TransactionsLogCoreService transactionsLogCoreService;
	
	@Autowired
	@Qualifier("MfinoServiceProviderCoreServiceImpl")
	private MfinoServiceProviderCoreService mfinoServiceProviderCoreService;
	
	private ChannelCode chanelCode;
	private ChannelCodeDAO channelCodeDAO = null;
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Override
	
	public void doReimburse() {
		log.info("FundReimburseServiceImpl:doReimburse():BEGIN");
			chanelCode = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_BackEnd);
			ChargeDefinitionQuery query =new ChargeDefinitionQuery();
			query.setFundingPartnerAndPocketNotNull(true);
			List<ChargeDefinition> chargeDefinations = chargeDefinitionService.get(query);
			log.info("FundReimburseServiceImpl:doReimburse():"+chargeDefinations.size());
			Map <Long,String> pocketIds =new HashMap<Long,String> ();
			for(ChargeDefinition chargedef:chargeDefinations){
				if(pocketIds.containsKey(chargedef.getPocket().getID())){
					continue;
				}else{
					pocketIds.put(chargedef.getPocket().getID(), "processed");
				}
				try{
					transferFunds(chargedef);
				}catch (Exception e) {
					log.info("Exception in fundreimbursement"+chargedef.getPocket().getID(),e);
				}
				log.info("FundReimburseServiceImpl:doReimburse():Complete");
			}
	}


	private void transferFunds(ChargeDefinition chargedef) {
		Pocket fundsPocket = chargedef.getPocket();
		fundsPocket = pocketService.getById(fundsPocket.getID());
		if(fundsPocket.getCurrentBalance().compareTo(BigDecimal.ZERO)==-1){
			SubscriberMDN partnerMdn = fundsPocket.getSubscriberMDNByMDNID();
			Pocket fundsSourcePocket = subscriberService.getDefaultPocket(partnerMdn.getID(), CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
			if(fundsSourcePocket!=null){
				String[] response=sendTransferInquiry(fundsSourcePocket,fundsPocket,partnerMdn);
				if(CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(response[0])
						&&StringUtils.isNotBlank(response[1])){
					sendTransferConfirmation(fundsSourcePocket, fundsPocket, partnerMdn,response);
				}

			}else{
				log.info("Bank Pocket NotFound");
			}
		}else{
			log.info("Funding pocket balance is not negative");
		}
	}


	private void sendTransferConfirmation(Pocket sourcePocket,
			Pocket destPocket, SubscriberMDN subMDN, String[] inquiryresponse) {

		CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
		transferConfirmation.setSourceMDN(subMDN.getMDN());
		transferConfirmation.setChannelCode(chanelCode.getChannelCode());
		transferConfirmation.setDestMDN(subMDN.getMDN());
		transferConfirmation.setDestPocketID(destPocket.getID());
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setSourcePocketID(sourcePocket.getID());
		transferConfirmation.setConfirmed(true);
		transferConfirmation.setTransferID(Long.valueOf(inquiryresponse[1]));
		transferConfirmation.setParentTransactionID(Long.valueOf(inquiryresponse[2]));
		transferConfirmation.setSourceApplication(chanelCode.getChannelSourceApplication());
		transferConfirmation.setServiceName(ServiceAndTransactionConstants.SERVICE_SYSTEM);
		transferConfirmation.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
		

		ServiceChargeTransactionLog sctl = transactionChargingService.getServiceChargeTransactionLog(transferConfirmation.getParentTransactionID());
		transferConfirmation.setServiceChargeTransactionLogID(sctl.getID());
		log.info("sending TransferConfirmation:");
		
		
		CMJSError response = (CMJSError) fundReimberseHandler.handle(transferConfirmation).getMultixResponse();
		String message = response.getErrorDescription();
		message = message.trim();
		log.info("Response:"+message);
	}


	private String[] sendTransferInquiry(Pocket sourcePocket,
			Pocket destPocket, SubscriberMDN subMDN) {
		CMBankAccountToBankAccount transferInquiry = new CMBankAccountToBankAccount();
		String [] result ={"0",null,null};
		transferInquiry.setPin("1234");		
		transferInquiry.setSourceApplication(chanelCode.getChannelSourceApplication());
		transferInquiry.setServiceName(ServiceAndTransactionConstants.SERVICE_SYSTEM);
		transferInquiry.setSourceMDN(subMDN.getMDN());
		transferInquiry.setAmount(destPocket.getCurrentBalance().negate());
		transferInquiry.setDestMDN(subMDN.getMDN());
		transferInquiry.setDestPocketID(destPocket.getID());
		transferInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferInquiry.setSourcePocketID(sourcePocket.getID());
		transferInquiry.setSourceMessage(ServiceAndTransactionConstants.TRANSACTION_FUNDREIMBURSE);
		transferInquiry.setChannelCode(chanelCode.getChannelCode());
		transferInquiry.setIsSystemIntiatedTransaction(CmFinoFIX.Boolean_True);
		
		TransactionsLog transactionsLog = saveTransactionsLog(CmFinoFIX.MessageType_FundReimburseInquiry, " ");
		transferInquiry.setTransactionID(transactionsLog.getID());
		
		// Generating the SCTL Entry 

		ServiceChargeTransactionLog sctl = new ServiceChargeTransactionLog();
		
		try{
			sctl.setCalculatedCharge(BigDecimal.ZERO);
			sctl.setChannelCodeID(chanelCode.getID());
			sctl.setSourceMDN(transferInquiry.getSourceMDN());
			sctl.setDestMDN(transferInquiry.getSourceMDN());
			sctl.setServiceID(transactionChargingService.getServiceId(transferInquiry.getServiceName()));
			sctl.setServiceProviderID(transactionChargingService.getServiceProviderId(null));
			sctl.setStatus(CmFinoFIX.SCTLStatus_Processing);
			sctl.setTransactionAmount(transferInquiry.getAmount());
			sctl.setTransactionTypeID(transactionChargingService.getTransactionTypeId(ServiceAndTransactionConstants.TRANSACTION_FUNDREIMBURSE));
			sctl.setTransactionID(transactionsLog.getID());
		} catch (InvalidServiceException ise) {
			log.error("Exception occured in getting charges",ise);
			result[0]=CmFinoFIX.NotificationCode_ServiceNotAvailable.toString();
			return result;
		}

		long sctlId = transactionChargingService.saveServiceTransactionLog(sctl);
		
		transferInquiry.setServiceChargeTransactionLogID(sctlId);
		log.info("sending TransferInquiry:");
		CMJSError response = (CMJSError) fundReimberseHandler.handle(transferInquiry).getMultixResponse();
		String message = response.getErrorDescription();
		message = message.trim();
		String code="0";
		String status = message.substring(0, 1);
		int startIndex = message.indexOf('(');
		int endIndex = message.indexOf(')');
		if (startIndex != -1 && endIndex != -1) {
			code = message.substring(startIndex + 1, endIndex);
		}
		result[0]=code;
		if ((CmFinoFIX.ResponseCode_Success.toString()).equals(status) && !("0".equals(code))) {
			result[1]=response.getTransferID().toString();
			result[2]=response.getParentTransactionID().toString();
			sctl.setCommodityTransferID(Long.valueOf(result[1]));
			sctl.setTransactionID(Long.valueOf(result[2]));
			transactionChargingService.completeTheTransaction(sctl);
		}
		else
		{
			transactionChargingService.failTheTransaction(sctl, code);
		}
		return result;

	}

	private TransactionsLog saveTransactionsLog(Integer messageCode, String data) {
		
		mFinoServiceProvider msp = mfinoServiceProviderCoreService.getById(1);
		
		TransactionsLog transactionsLog = new TransactionsLog();
		transactionsLog.setMessageCode(messageCode);
		transactionsLog.setMessageData(data);
		transactionsLog.setmFinoServiceProviderByMSPID(msp);
		transactionsLog.setTransactionTime(new Timestamp(new Date()));
		transactionsLogCoreService.save(transactionsLog);
		return transactionsLog;
	}
	
}
