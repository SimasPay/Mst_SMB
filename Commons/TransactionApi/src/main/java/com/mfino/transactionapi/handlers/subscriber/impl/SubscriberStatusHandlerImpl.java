/**
 * 
 */
package com.mfino.transactionapi.handlers.subscriber.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
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
		
		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(txnDetails.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);

		addCompanyANDLanguageToResult(srcSubscriberMDN, result);
		result.setSourceMDN(txnDetails.getSourceMDN());
		result.setStatus(String.valueOf(validationResult));
		
		log.info("Subscriber status Validation Result for "+txnDetails.getSourceMDN()+" is "+validationResult);
		
		
		if(validationResult.equals(CmFinoFIX.ResponseCode_Success))
		{
			Subscriber subscriber = srcSubscriberMDN.getSubscriber();
			result.setFirstName(subscriber.getFirstName());
			result.setLastName(subscriber.getLastName());
		}
		
		log.info("Subscriber Status request: END");
		return result;
	}
}
