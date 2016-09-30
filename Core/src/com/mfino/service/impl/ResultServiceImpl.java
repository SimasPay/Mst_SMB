package com.mfino.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.result.Result;
import com.mfino.result.SMSResult;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.ResultService;
import com.mfino.service.SMSService;
@Service("ResultServiceImpl")
public class ResultServiceImpl implements ResultService{
	private static Logger log = LoggerFactory.getLogger(ResultService.class);


	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService; 
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	

	/**
	 * Sends an SMS to the number that is returned by base.getSourceMDN() <br>
	 * @param base
	 *            base messages used in handlers
	 * @param result
	 *            result returned by createResult()
	 */

	public void sendSMS(CMBase base, Result result) {
		// setting the sourcemdn when smsresult.render() is called
		smsService.setSctlId(base.getServiceChargeTransactionLogID());
		smsService.setDestinationMDN(base.getSourceMDN());
		SMSResult smsResult = cloneResultAsSMSResult(result);
		try {
			smsResult.setSMSService(smsService);
			smsResult.setNotificationMessageParserService(notificationMessageParserService);
			smsResult.setNotificationService(notificationService);
			smsResult.render();
		}
		catch (Exception error) {
			log.error("Sending SMS failed", error);
		}
	}
	
	public Result returnResult(CMBase msg, Integer nc, SubscriberMdn smdn, Result result, boolean sms) {
		result.setNotificationCode(nc);
		
		if (sms)
			sendSMS(msg, result);
		return result;
	}



	/**
	 * Copies the result as an SMSResult. Sets all the inherited fields <br>
	 * of SMSResult object using the fields of Result object. <br>
	 * 
	 * @author Gurram Karthik
	 * 
	 * @param result
	 *            The result that is to be cloned as SMSResult
	 * 
	 * @return SMSResult the new SMSResult created after cloning has been done
	 */
	public SMSResult cloneResultAsSMSResult(Result result) {
		SMSResult smsresult = new SMSResult();
		smsresult.setCompany(result.getCompany());
		smsresult.setLanguage(result.getLanguage());
		smsresult.setMultixResponse(result.getMultixResponse());
		smsresult.setNotificationCode(result.getNotificationCode());
		smsresult.setPocketList(result.getPocketList());
		smsresult.setSourceMessage(result.getSourceMessage());
		smsresult.setTransactionList(result.getTransactionList());
		smsresult.setLastBankTrxnList(result.getLastBankTrxnList());
		return smsresult;
	}


}
