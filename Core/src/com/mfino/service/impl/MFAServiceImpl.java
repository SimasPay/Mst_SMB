package com.mfino.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFAAuthenticationDAO;
import com.mfino.dao.MFATransactionInfoDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.MFAAuthenticationQuery;
import com.mfino.dao.query.MFATransactionInfoQuery;
import com.mfino.domain.MFAAuthentication;
import com.mfino.domain.MFATransactionInfo;
import com.mfino.domain.Service;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.MFAService;
import com.mfino.service.SMSService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.MfinoUtil;

@org.springframework.stereotype.Service("MFAServiceImpl")
public class MFAServiceImpl implements MFAService{

	private static Logger log = LoggerFactory.getLogger(MFAServiceImpl.class);
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean isMFATransaction(String serviceName, String transactionName, Long channelCodeId){
		log.info("MFAService::isMFATransaction Begin");
		MFATransactionInfoDAO dao = DAOFactory.getInstance().getMfaTransactionInfoDAO();
		MFATransactionInfoQuery mfaQuery = new MFATransactionInfoQuery();
		Service service = DAOFactory.getInstance().getServiceDAO().getServiceByName(serviceName);
		Long serviceId = service.getID();
		TransactionType transactionType = DAOFactory.getInstance().getTransactionTypeDAO().getTransactionTypeByName(transactionName);
		Long transactionTypeId = transactionType.getID();
		mfaQuery.setTransactionTypeId(transactionTypeId);
		mfaQuery.setServiceId(serviceId);
		mfaQuery.setChannelCodeId(channelCodeId);
		mfaQuery.setMfaMode(CmFinoFIX.MFAMode_OTP);
		List <MFATransactionInfo> transactionList = dao.get(mfaQuery);
		if(transactionList.size() > 0){
			log.info("MFAService::isMFATransaction = true");
			log.info("MFAService::isMFATransaction End");
			return true;
		}
		log.info("MFAService::isMFATransaction End");
		return false;
	}
	
	/**
	 * Since the message for Bahasa and English is same, we dont need to get user's language.
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void handleMFATransaction(Long sctlID, String sourceMDN){
		log.info("MFAService::handleMFATransaction Begin");
		Integer OTPLength = systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(OTPLength);
		String digestPin1 = MfinoUtil.calculateDigestPin(sourceMDN, oneTimePin);
		MFAAuthentication mfaAuth = new MFAAuthentication();
		mfaAuth.setSctlId(sctlID);
		mfaAuth.setMFAMode(CmFinoFIX.MFAMode_OTP);
		mfaAuth.setMFAValue(digestPin1);
		
		MFAAuthenticationDAO authDAO = DAOFactory.getInstance().getMfaAuthenticationDAO();
		authDAO.save(mfaAuth);
		
		String message = "Your Simobi Code is "
				+ oneTimePin+ "(ref no: "+ sctlID
				+ "). WASPADAI PENIPUAN! JANGAN berikan kode ini kepada siapapun! Bank Sinarmas CARE 1500153";
		
		smsService.setDestinationMDN(sourceMDN);
		smsService.setMessage(message);
		smsService.setNotificationCode(CmFinoFIX.NotificationCode_New_OTP_Success);
		smsService.asyncSendSMS();
		log.info("MFAService::handleMFATransaction End");
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean isValidOTP(String otp, Long sctlID, String sourceMdn){
		log.info("MFAService::handleMFATransaction Begin");
		String oneTimePin = otp;
		String digestPin1 = MfinoUtil.calculateDigestPin(sourceMdn, oneTimePin);
		MFAAuthenticationDAO authDAO = DAOFactory.getInstance().getMfaAuthenticationDAO();
		MFAAuthenticationQuery query = new MFAAuthenticationQuery();
		query.setSctlId(sctlID);
		query.setMfaMode(CmFinoFIX.MFAMode_OTP);
		query.setMfaValue(digestPin1);
		List <MFAAuthentication> mfaLst = authDAO.get(query);
		if(mfaLst.size() > 0){
			log.info("MFAService::isValidOTP = true");
			log.info("MFAService::isValidOTP End");
			return true;
		}
		log.info("MFAService::isValidOTP End");
		return false;			
	}
}
