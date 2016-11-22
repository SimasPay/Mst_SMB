/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.SubscriberMdnService;
import com.mfino.transactionapi.handlers.subscriber.SubscriberStatusHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberStatusXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * This handler validates if a Subscriber with given MDN exists, if yes returns Subscriber details such as FirstName, LastName, Status.
 * 
 * If no returns result as negative.
 * 
 * @author Chaitanya
 *
 */
@Service("SubscriberStatusHandlerImpl")
public class SubscriberStatusHandlerImpl extends FIXMessageHandler implements SubscriberStatusHandler{
	
	private static Logger	log	= LoggerFactory.getLogger(SubscriberStatusHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	public Result handle(TransactionDetails txnDetails)
	{
		log.info("Subscriber Status request: BEGIN");
		SubscriberStatusXMLResult result = new SubscriberStatusXMLResult();
		
		SubscriberMdn srcSubscriberMDN = subscriberMdnService.getByMDN(txnDetails.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);

		addCompanyANDLanguageToResult(srcSubscriberMDN, result);
		result.setSourceMDN(txnDetails.getSourceMDN());
		result.setStatus(String.valueOf(validationResult));
		
		log.info("Subscriber status Validation Result for "+txnDetails.getSourceMDN()+" is "+validationResult);
		
		Subscriber subscriber = srcSubscriberMDN!=null ? srcSubscriberMDN.getSubscriber() : null;
		if(subscriber != null) {
			result.setFirstName(subscriber.getFirstname());
			result.setLastName(subscriber.getLastname());
			String kycLevel = subscriber.getKycLevel().getKyclevel().toString();
			result.setKycLevel(kycLevel);
			String txnList = "All";
			String alertMsg = "";
			if (CmFinoFIX.SubscriberKYCLevel_NoKyc.toString().equals(kycLevel)) {
				String allowedTxnList[] = getAllowedTxnList(kycLevel);
				txnList = allowedTxnList[0];
				alertMsg = allowedTxnList[1];
			}
			result.setAllowedTxns(txnList);
			result.setMessage(alertMsg);
			result.setNotificationCode(CmFinoFIX.ResponseCode_Success);
		} else {
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNNotFound);
		}
		
		log.info("Subscriber Status request: END");
		return result;
	}
	
	/**
	 * Returns the allowed txns list for the given KYC level.
	 * @param kycLevel
	 * @return
	 */
	private String[] getAllowedTxnList(String kycLevel) {
		String result[] = new String[2];
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(new File("../mfino_conf", "kyc_txn_list.json")));
			String curLine = null;
			while((curLine=br.readLine()) != null){
				sb.append(curLine);
			}
			br.close();
			JSONObject jsonObject =  new JSONObject(sb.toString());
			JSONObject kycJson = jsonObject.getJSONObject(kycLevel);
			result[0] = kycJson.getString("allowedTxns");
			result[1] = kycJson.getString("alertMessage");
		} catch (FileNotFoundException e) {
			log.error("Error: FileNotFoundException While reading kyc_txn_list.json file");
		} catch (IOException e) {
			log.error("Error: IOException While reading kyc_txn_list.json file");
		} catch (JSONException e) {
			log.error("Error: JSONException While reading kyc_txn_list.json file");
		}
		return result;
	}
}
