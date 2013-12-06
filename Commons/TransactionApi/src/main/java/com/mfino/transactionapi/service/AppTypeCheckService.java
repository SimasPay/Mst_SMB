package com.mfino.transactionapi.service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;

public class AppTypeCheckService {
	
  public static boolean appTypeCheck(SubscriberMDN mdn, String appType){
		
		if ((CmFinoFIX.SubscriberType_Partner.equals(mdn.getSubscriber().getType()) && ServiceAndTransactionConstants.APP_TYPE_AGENT.equals(appType))
		     ||(CmFinoFIX.SubscriberType_Subscriber.equals(mdn.getSubscriber().getType()) && ServiceAndTransactionConstants.APP_TYPE_SUBSCRIBER.equals(appType)))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
