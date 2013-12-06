/**
 * 
 */
package com.mfino.validators;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Merchant;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Deva
 *
 */
public class DestMerchantValidator implements IValidator {

private SubscriberMDN subscriberMDN;
	
	private String mdn;
	
	/**
	 * 
	 */
	
	public DestMerchantValidator(SubscriberMDN subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}
	
	/* (non-Javadoc)
	 * @see com.mfino.validators.IValidator#validate()
	 */
	@Override
	public Integer validate() {
		if (subscriberMDN == null) {
			SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			subscriberMDN = subscriberMDNDAO.getByMDN(mdn);
			if (subscriberMDN == null) {
				return CmFinoFIX.NotificationCode_MDNNotFound;
			}
		}
		Merchant merchant = subscriberMDN.getSubscriber().getMerchant();
        if(merchant == null){
            return CmFinoFIX.NotificationCode_MDNNotFound;
        }
		if (!CmFinoFIX.SubscriberStatus_Active.equals(merchant.getStatus()) 
				/* && !CmFinoFIX.SubscriberStatus_PendingRetirement.equals(merchant.getStatus()) */
			){
				 
			return CmFinoFIX.NotificationCode_DestinationMDNIsNotActive;
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

	/**
	 * @return the mdn
	 */
	public String getMdn() {
		return mdn;
	}

	/**
	 * @param mdn the mdn to set
	 */
	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

}
