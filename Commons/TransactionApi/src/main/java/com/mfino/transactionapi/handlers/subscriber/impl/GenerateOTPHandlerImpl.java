package com.mfino.transactionapi.handlers.subscriber.impl;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MdnOtpDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MdnOtp;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGenerateOTP;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.impl.SMSServiceImpl;
import com.mfino.transactionapi.handlers.subscriber.GenerateOTPHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.GenerateOtpXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Amar
 *
 */
@Service("GenerateOTPHandlerImpl")
public class GenerateOTPHandlerImpl extends FIXMessageHandler implements GenerateOTPHandler{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService ;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

 	public XMLResult handle(TransactionDetails txnDetails) {
		 ChannelCode cc= txnDetails.getCc();
		 CMGenerateOTP  generateOTP = new CMGenerateOTP();
		generateOTP.setMDN(txnDetails.getSourceMDN());
		generateOTP.setChannelCode(cc.getChannelCode());
		generateOTP.setSourceApplication(cc.getChannelSourceApplication());
		generateOTP.setTransactionIdentifier(txnDetails.getTransactionIdentifier());

		TransactionsLog transactionsLog = null;
		log.info("Handling GenerateOTP webapi request");
		XMLResult result = new GenerateOtpXMLResult();
		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_GenerateOTP,generateOTP.DumpFields());
		result.setSourceMessage(generateOTP);
		result.setDestinationMDN(generateOTP.getMDN());
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());
		generateOTP.setTransactionID(transactionsLog.getID());
		result.setActivityStatus(false);
		SubscriberMDN subscriberMDN = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(txnDetails.getSourceMDN());
		if(subscriberMDN != null && !subscriberMDN.getStatus().equals(CmFinoFIX.MDNStatus_NotRegistered))
		{
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Source);
			result.setFirstName(subscriberMDN.getSubscriber().getFirstName());
			result.setLastName(subscriberMDN.getSubscriber().getLastName());
			result.setNickName(subscriberMDN.getSubscriber().getNickname());
			return result;
		}
		MdnOtpDAO mdnOtpDao = DAOFactory.getInstance().getMdnOtpDAO();
		MdnOtp mdnOtp = new MdnOtp();
		mdnOtp.setMDN(subscriberService.normalizeMDN(generateOTP.getMDN()));

		int otpLength=systemParametersService.getOTPLength();
		String oneTimePin = MfinoUtil.generateOTP(otpLength);
		String digestPin = MfinoUtil.calculateDigestPin(mdnOtp.getMDN(), oneTimePin);
		mdnOtp.setOTP(digestPin);
		mdnOtp.setStatus(CmFinoFIX.OTPStatus_Initialized);
		mdnOtp.setOTPExpirationTime(new Timestamp(DateUtil.addHours(new Date(), systemParametersService.getInteger(SystemParameterKeys.REGISTRATION_OTP_TIMEOUT_DURATION))));
		mdnOtp.setOtpRetryCount(0);
		mdnOtpDao.save(mdnOtp);
		result.setActivityStatus(false);
		result.setNotificationCode(CmFinoFIX.NotificationCode_New_OTP_Success);
		result.setOneTimePin(oneTimePin);
		result.setOtpExpirationTime(mdnOtp.getOTPExpirationTime());
		result.setIdNumber(mdnOtp.getID().toString());
		sendSMS(generateOTP.getMDN(), oneTimePin, result.getOtpExpirationTime(),mdnOtp.getID());
		log.info("OTP generation successful for MDN " + generateOTP.getMDN());
		return result;

	}


	private void sendSMS(String mdn, String oneTimePin, String otpExpirationTime, Long tokenID) {
		SMSServiceImpl smsService = new SMSServiceImpl();
		//smsService.setSctlId(mdnOtp.getServiceChargeTransactionLogID());		
//		String message = "<st>" + tokenID + "###" + oneTimePin + "</st>";
		String message = "Kode verifikasi Uangku anda adalah " + oneTimePin + " (no ref: " + tokenID + ")";
		smsService.setDestinationMDN(mdn);
		smsService.setMessage(message);
		smsService.send();
		
	}
}
