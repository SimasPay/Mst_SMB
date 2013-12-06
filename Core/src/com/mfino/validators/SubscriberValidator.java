/**
 * 
 */
package com.mfino.validators;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;


/**
 * @author Deva
 *
 */

/*
 * 1. Validates if the Subscriber MDN exists in DB.
 * 2. Validates if the Subscriber exists.
 * 3. Validates MSPID for the subscriber.
 * 4. Validate if the subscriber has valid PIN and 
 * 5. Validates if the Subscriber is active or Not.
 *  
 */
public class SubscriberValidator implements IValidator{

	private String mdn = null;
	
	private SubscriberMDN subscriberMDN;
	
	private Subscriber subscriber;
	
	/**
	 * 
	 */
	public SubscriberValidator(String mdn) {
		this.mdn = mdn;
	}
	/* (non-Javadoc)
	 * @see com.mfino.validators.Validator#validate()
	 */
	@Override
	public Integer validate() {
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		subscriberMDN = subscriberMDNDAO.getByMDN(mdn);
		if (subscriberMDN == null) {
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		subscriber = subscriberMDN.getSubscriber();
		if ( subscriber == null ) {
			return CmFinoFIX.NotificationCode_MDNNotFound;
		} 
		if (subscriber.getmFinoServiceProviderByMSPID().getID() != 1L) {
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		return CmFinoFIX.ResponseCode_Success;
	}
	/**
	 * @return the subscriberMDN
	 */
	public SubscriberMDN getSubscriberMDN() {
		return subscriberMDN;
	}
	/**
	 * @param subscriberMDN the subscriberMDN to set
	 */
	public void setSubscriberMDN(SubscriberMDN subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}
	
	public static void main(String[] args) {
		SubscriberValidator subscriberValidator = new SubscriberValidator("6288116210961");
    	Integer validationResult = subscriberValidator.validate();
    	System.out.println("Validation Result " + validationResult);
	}
}
