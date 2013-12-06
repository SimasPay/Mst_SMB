/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.Subscriber;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSDistributeChargesForm;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.service.SubscriberService;
import com.mfino.transactionapi.service.HierarchyTxnService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.DistributeChargesProcessor;

/**
 * @author Amar
 */
@Service("DistributeChargesProcessorImpl")
public class DistributeChargesProcessorImpl extends BaseFixProcessor implements DistributeChargesProcessor{

	private static String delimiter = ",";

	@Autowired
	@Qualifier("HierarchyTxnServiceImpl")
	private HierarchyTxnService txnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
		int errorCode = CmFinoFIX.ErrorCode_NoError;
		String description = "Amount transferred Successfully";
		
		CMJSDistributeChargesForm realMsg = (CMJSDistributeChargesForm) msg;
		
		String amountStr = realMsg.getAmountPerPartner();
		BigDecimal amt = BigDecimal.ZERO;
		try
		{
			amt = new BigDecimal(amountStr);
		}
		catch(NumberFormatException nfe)
		{
			errorCode = CmFinoFIX.ErrorCode_InvalidAmount;
			description = "Amount specified is invalid";
			return getError(errorCode, description);
		}
		
		Long subscriberId = realMsg.getSourceSubscriberID();
		if(subscriberId==null)
		{
			errorCode = CmFinoFIX.ErrorCode_SourceMDNNotFound;
			description = "Source MDN not found";
			return getError(errorCode, description);
		}
		String selectedSubIds = realMsg.getListOfSelectedPartners();
		if(selectedSubIds==null)
		{
			errorCode = CmFinoFIX.ErrorCode_DestMDNNotFound;
			description = "Destination MDN not found";
			return getError(errorCode, description);
		}
		String pin = realMsg.getPin();
		if(pin==null)
		{
			errorCode = CmFinoFIX.ErrorCode_RequiredParametersMissing;
			description = "Pin not specified";
			return getError(errorCode, description);
		}
		
		Subscriber sourceSubscriber = subscriberService.getSubscriberbySubscriberId(subscriberId);
		
		String[] childSubIds = selectedSubIds.split(delimiter);
		
		Map<Subscriber, BigDecimal> childSubVsAmt = new HashMap<Subscriber, BigDecimal>();
		BigDecimal sumAmt = BigDecimal.ZERO;
		
		for(int index=0; index<childSubIds.length; index++)
		{
			long subId = Long.valueOf(childSubIds[index]);
			Subscriber childSub = subscriberService.getSubscriberbySubscriberId(subId);
			if(childSub!=null)
			{
				childSubVsAmt.put(childSub, amt);
				sumAmt = sumAmt.add(amt);
			}
			else
			{
				errorCode = CmFinoFIX.ErrorCode_DestMDNNotFound;
				description = "Destination MDN not found for id: "+subId;
				return getError(errorCode, description);
			}
		}
		
		
		Map<Subscriber, CMJSError> result = new HashMap<Subscriber, CMJSError>();
		if(sourceSubscriber!=null)
		{
			result = txnService.transferToChildren(sourceSubscriber, childSubVsAmt, pin, sumAmt, realMsg.getDCTID());
		}
		else
		{
			errorCode = CmFinoFIX.ErrorCode_SourceMDNNotFound;
			description = "Source MDN not found for id: "+subscriberId;
			return getError(errorCode, description);
		}

		if(!result.isEmpty())
		{
			Set<Entry<Subscriber, CMJSError>> set = result.entrySet();
			Iterator<Entry<Subscriber, CMJSError>> iterator = set.iterator();
			description = "";
			while(iterator.hasNext())
			{
				errorCode = CmFinoFIX.ErrorCode_Generic;
				Entry<Subscriber, CMJSError> entry = iterator.next();
				description = description+"\n"+"Subscriber: "+entry.getKey().getFirstName()+" Message: "+entry.getValue().getErrorDescription(); 
			}
		}
		return getError(errorCode, description);
	}
	
	private CMJSError getError(int errorCode, String description)
	{
		CMJSError error = new CMJSError();
		error.setErrorCode(errorCode);
		error.setErrorDescription(MessageText._(description));
		if(errorCode==CmFinoFIX.ErrorCode_NoError)
		{
			log.info(description);
		}
		else
		{
			log.error("ErrorCode: "+errorCode+" message: "+description);
		}
		return error;
	}
}
