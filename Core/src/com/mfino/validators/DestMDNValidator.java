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
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;

/**
 * @author Deva
 *
 */

public class DestMDNValidator implements IValidator{

	private SubscriberMdn subscriberMDN;
	
	private String mdn;
	
	private Subscriber subscriber = null;
	
	public DestMDNValidator(SubscriberMdn subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}
	
	public DestMDNValidator(String mdn) {
		this.mdn = mdn;
	}
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

	/* (non-Javadoc)
	 * @see com.mfino.validators.Validator#validate()
	 */
	@Override
	public Integer validate() {
		
		if (subscriberMDN == null) {
			SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			subscriberMDN = subscriberMDNDAO.getByMDN(mdn);
			if (subscriberMDN == null) {
				return CmFinoFIX.NotificationCode_DestinationMDNNotFound;
			}
			setSubscriberMDN(subscriberMDN);
		}
		
		subscriber = subscriberMDN.getSubscriber();
		
		if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriber.getStatus()))
		{
			return CmFinoFIX.NotificationCode_SubscriberNotRegistered;
		}
		if (!CmFinoFIX.MDNStatus_Active.equals(subscriberMDN.getStatus())
				&&!CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())) {
			if(!(CmFinoFIX.SubscriberRestrictions_NoFundMovement.equals(subscriberMDN.getRestrictions()))){
				return CmFinoFIX.NotificationCode_DestinationMDNIsNotActive;
			}
		} 
		//  && CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(subscriber.getRestrictions()) 
		if (CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(subscriberMDN.getRestrictions())) {
			return CmFinoFIX.NotificationCode_DestinationMDNIsRestricted;
		}
		return CmFinoFIX.ResponseCode_Success;
	}

	public SubscriberMdn getSubscriberMDN() {
		return subscriberMDN;
	}

	public void setSubscriberMDN(SubscriberMdn subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}

	public Subscriber getSubscriber(){
		return subscriber;
	}
	
}
