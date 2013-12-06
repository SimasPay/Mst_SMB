package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.ExpirationType;
import com.mfino.domain.FundDefinition;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.scheduler.service.ReverseFundsService;
import com.mfino.service.FundStorageService;
import com.mfino.service.MfinoService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PartnerService;
import com.mfino.service.SMSService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.transactionapi.handlers.wallet.ReverseDistributionHandler;
import com.mfino.transactionapi.handlers.wallet.ReverseFundsHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.util.MfinoUtil;

@Service("ReverseFundsServiceImpl")
public class ReverseFundsServiceImpl implements ReverseFundsService {
	
	@Autowired
	@Qualifier("ReverseFundsHandlerImpl")
	private ReverseFundsHandler reverseFundsHandler;
	
	@Autowired
	@Qualifier("FundStorageServiceImpl")
	private FundStorageService fundStorageService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("UnRegisteredTxnInfoServiceImpl")
	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;
	
	@Autowired
	@Qualifier("ReverseDistributionHandlerImpl")
	private ReverseDistributionHandler reverseDistributionHandler;
	
	@Autowired
	 @Qualifier("PartnerServiceImpl")
	 private PartnerService partnerService;
	 
	 @Autowired
	 @Qualifier("NotificationServiceImpl")
	 private NotificationService notificationService;
	 
	 @Autowired
	 @Qualifier("MfinoServiceImpl")
	 private MfinoService mfinoService;
	 
	 @Autowired
	 @Qualifier("NotificationMessageParserServiceImpl")
	 private NotificationMessageParserService notificationMessageParserService;
	 
		private HibernateTransactionManager txManager;
		
		public HibernateTransactionManager getTxManager() {
			return txManager;
		}

		public void setTxManager(HibernateTransactionManager txManager) {
			this.txManager = txManager;
		}

	private static final Logger log = LoggerFactory.getLogger(ReverseFundsServiceImpl.class);

	public void reverseFunds(){
		log.info("ReverseFundsServiceImpl :: reverseFunds :: BEGIN reversing funds");
			reverseDistributionHandler.handle();
			callFundReversalHandler();
		log.info("ReverseFundsServiceImpl :: reverseFunds :: END reversing funds");
	}
	
	private void callFundReversalHandler() {
		Integer[] status = new Integer[3]; 
		status[0] = CmFinoFIX.UnRegisteredTxnStatus_FUNDALLOCATION_COMPLETE;
		status[1] = CmFinoFIX.UnRegisteredTxnStatus_FUND_PARTIALLY_WITHDRAWN;
		UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
		urtiQuery.setMultiStatus(status);
		urtiQuery.setExpiryTime(new Timestamp());
		List<UnRegisteredTxnInfo> lstUnRegisteredTxnInfos = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(urtiQuery);
		UnRegisteredTxnInfo unRegisteredTxnInfo;
		
		if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
			log.info("Checking for expiry time of all funds");
			expireFunds(lstUnRegisteredTxnInfos);
		}else{
			log.info("No funds found for expiring");
		}
		lstUnRegisteredTxnInfos.clear();
		UnRegisteredTxnInfoQuery unRegisteredTxnInfoQuery = new UnRegisteredTxnInfoQuery();
		Integer requiredStatus = CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_INITIALIZED;
		unRegisteredTxnInfoQuery.setStatus(requiredStatus);
		lstUnRegisteredTxnInfos = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(unRegisteredTxnInfoQuery);
		if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
			for(int iter=0;iter<lstUnRegisteredTxnInfos.size();iter++){
				unRegisteredTxnInfo = lstUnRegisteredTxnInfos.get(iter);
				if(ServiceAndTransactionConstants.TRANSACTION_FUND_ALLOCATION.equals(unRegisteredTxnInfo.getTransactionName())){
					if(BigDecimal.ZERO.compareTo(unRegisteredTxnInfo.getAvailableAmount())==-1){
						XMLResult result = (XMLResult)reverseFundsHandler.handle(unRegisteredTxnInfo);
					}
					else{
						log.info("Zero amount for reversal.Reversal will not be done");
					}
			    }

			}
		}
	}

	private void expireFunds(List<UnRegisteredTxnInfo> lstUnRegisteredTxnInfos) {
		UnRegisteredTxnInfo unRegisteredTxnInfo;
		for(int iter=0;iter<lstUnRegisteredTxnInfos.size();iter++){
			unRegisteredTxnInfo = lstUnRegisteredTxnInfos.get(iter);
				log.info("Fund with ID: "+unRegisteredTxnInfo.getID()+"has expired");
				updateStatus(unRegisteredTxnInfo);
		}
	}
	
	private void updateStatus(UnRegisteredTxnInfo unRegisteredTxnInfo) {

		XMLResult result = new TransferInquiryXMLResult();
		result.setNotificationMessageParserService(notificationMessageParserService);
		result.setMfinoService(mfinoService);
		result.setPartnerService(partnerService);
		result.setNotificationService(notificationService);
		FundDefinition fundDefinition = unRegisteredTxnInfo.getFundDefinition();
		if(fundDefinition.getFundEventsByOnFundAllocationTimeExpiry().getFundEventType().equals(CmFinoFIX.FundEventType_Reversal)){//reversal
			log.info("Reversing allocated fund.Reversing transaction with sctlID as:"+unRegisteredTxnInfo.getTransferSCTLId());
			unRegisteredTxnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_INITIALIZED);
			unRegisteredTxnInfo.setReversalReason("Fund Expired");
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundAllocatedExpiredReversal);
			sendSms(unRegisteredTxnInfo.getWithdrawalMDN(), result);
		}else if(fundDefinition.getFundEventsByOnFundAllocationTimeExpiry().getFundEventType().equals(CmFinoFIX.FundEventType_RegenerateFACAuto)){//auto fac regen
			log.info("regerating fac.....");
			unRegisteredTxnInfo.setExpiryTime(getNewExpiryTime(fundDefinition.getExpirationTypeByExpiryID()));
			result.setOneTimePin(regenerateFAC(unRegisteredTxnInfo));
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundAllocatedExpiredNewFac);
			sendSms(unRegisteredTxnInfo.getWithdrawalMDN(), result);

		}else if(fundDefinition.getFundEventsByOnFundAllocationTimeExpiry().getFundEventType().equals(CmFinoFIX.FundEventType_RegenerateFACManual)){//manual fac regen
			log.info("regenerate fac manually");
			unRegisteredTxnInfo.setExpiryTime(getNewExpiryTime(fundDefinition.getExpirationTypeByExpiryID()));
			result.setNotificationCode(CmFinoFIX.NotificationCode_FundAllocatedExpired);
			sendSms(unRegisteredTxnInfo.getWithdrawalMDN(), result);
		}
		unRegisteredTxnInfoService.save(unRegisteredTxnInfo);
	}

	
	private Timestamp getNewExpiryTime(ExpirationType expirationType) {
		Long defaultExpirySeconds = 86400L;
		if(expirationType.getExpiryType().equals(CmFinoFIX.ExpiryType_Fund)){
			if(expirationType.getExpiryMode().equals(CmFinoFIX.ExpiryMode_DurationInSecs)){
				return new Timestamp(System.currentTimeMillis() + expirationType.getExpiryValue() * 1000);
			}else if(expirationType.getExpiryMode().equals(CmFinoFIX.ExpiryMode_CutOffTime)){
			}
		}
		log.debug("Could not find Fund related expiry time.setting a deafult of 1 days");
		return new Timestamp(System.currentTimeMillis() +  defaultExpirySeconds* 1000);		
	}
	
	public String regenerateFAC(UnRegisteredTxnInfo unRegisteredTxnInfo) {
		String code = generateFAC(unRegisteredTxnInfo.getFundDefinition());
		String digestedCode = generateDigestedFAC(unRegisteredTxnInfo.getWithdrawalMDN(), code);
		unRegisteredTxnInfo.setDigestedPIN(digestedCode);
		unRegisteredTxnInfoService.save(unRegisteredTxnInfo);
		return code;
	}
	
	public String generateFAC(FundDefinition fundDef) {
		return fundStorageService.generateFundAccessCode(fundDef);
	}

	public String generateDigestedFAC(String subscriberMDN, String code) {
		return MfinoUtil.calculateDigestPin(subscriberMDN, code);
	}
	
	private void sendSms(String destMDN,XMLResult result){
		String message = null;
		try {
			result.buildMessage();
			StringBuilder sb = new StringBuilder("(");
			sb.append(result.getXMlelements().get("code"));
			sb.append(")");
			sb.append(result.getXMlelements().get("message"));
			message = sb.toString();
		} catch (XMLStreamException e) {
			log.error("Error While parsing the Result...", e);
		}

		smsService.setDestinationMDN(destMDN);
		smsService.setMessage(message);
		smsService.setNotificationCode(result.getNotificationCode());
		smsService.asyncSendSMS();
	}

	

}
