/**
 * 
 */
package com.mfino.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.SubscriberService;

/**
 * @author Deva
 * 
 */

/*
 * 1. Validates if the Source Subscriber MDN Status is Active or Pendingretired.
 * 2. Validates if the Source Subscriber MDN has restriction.
 */
public class SourceMDNValidator implements IValidator {
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	private SubscriberMdn subscriberMDN;

	private String mdn;

	/**
	 * 
	 */
	public SourceMDNValidator(SubscriberMdn subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}

	public SourceMDNValidator(String mdn) {
		this.mdn = subscriberService.normalizeMDN(mdn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.validators.Validator#validate()
	 */
	@Override
	public Integer validate() {
		if (subscriberMDN == null) {
			SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			subscriberMDN = subscriberMDNDAO.getByMDN(mdn);
			if (subscriberMDN == null) {
				return CmFinoFIX.NotificationCode_MDNNotFound;
			}
			setSubscriberMDN(subscriberMDN);
		}
		Subscriber subscriber = subscriberMDN.getSubscriber();
		if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriber.getStatus()))
		{
			return CmFinoFIX.NotificationCode_SubscriberNotRegistered;
		}
//		if (!CmFinoFIX.MDNStatus_Active.equals(subscriberMDN.getStatus())
//				/* && !CmFinoFIX.MDNStatus_PendingRetirement.equals(subscriberMDN.getStatus()) */
//				&& !CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())
//				/* && !CmFinoFIX.SubscriberStatus_PendingRetirement.equals(subscriber.getStatus()) */
//				) {
//
//			return CmFinoFIX.NotificationCode_MDNIsNotActive;
//		} 
		
		// First Restrictions should be checked as we are allowing InActive Subscribers(of no activity) to login
		if (!(CmFinoFIX.SubscriberRestrictions_None.equals(subscriberMDN.getRestrictions())) &&
				!(CmFinoFIX.SubscriberRestrictions_NoFundMovement.equals(subscriberMDN.getRestrictions()))) {
			return CmFinoFIX.NotificationCode_MDNIsRestricted;
		}
		
		if (!isValidActiveOrValidInActiveSubscriber()) {
			return CmFinoFIX.NotificationCode_MDNIsNotActive;
		}
		
		
		return CmFinoFIX.ResponseCode_Success;
	}

	public SubscriberMdn getSubscriberMDN() {
		return subscriberMDN;
	}

	public void setSubscriberMDN(SubscriberMdn subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}
	
	private boolean isValidActiveOrValidInActiveSubscriber(){
		Subscriber subscriber = subscriberMDN.getSubscriber();
		if(CmFinoFIX.MDNStatus_Active.equals(subscriberMDN.getStatus())&& CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())){
			return true;
		}
		if(CmFinoFIX.MDNStatus_InActive.equals(subscriberMDN.getStatus())&& CmFinoFIX.SubscriberStatus_InActive.equals(subscriber.getStatus())){
			
			return true;
		}	
		return false;
	}

}
