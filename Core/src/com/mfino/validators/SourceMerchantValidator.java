/**
 * 
 */
package com.mfino.validators;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Merchant;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Deva
 * 
 * For Now we need only Source merchant validation. So just adding Source merchant validator
 *
 */
public class SourceMerchantValidator implements IValidator {

	private SubscriberMdn subscriberMDN;
	
	private String mdn;
	
	/**
	 * 
	 */
	
	public SourceMerchantValidator(SubscriberMdn subscriberMDN) {
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
            return CmFinoFIX.NotificationCode_MDNIsNotActive;
        }
        if(CmFinoFIX.SubscriberStatus_Initialized.equals(merchant.getStatus()))
        {
        	merchant.setStatus(CmFinoFIX.SubscriberStatus_Active);
        	MerchantDAO merchantDAO = DAOFactory.getInstance().getMerchantDAO();
        	merchantDAO.save(merchant);
        	return CmFinoFIX.ResponseCode_Success;
         }
		if (!CmFinoFIX.SubscriberStatus_Active.equals(merchant.getStatus()) 
				/*&& !CmFinoFIX.SubscriberStatus_PendingRetirement.equals(merchant.getStatus())*/
				) {
			return CmFinoFIX.NotificationCode_MDNIsNotActive;
		} 
		return CmFinoFIX.ResponseCode_Success;
	}

	/**
	 * @return the subscriberMDN
	 */
	public SubscriberMdn getSubscriberMDN() {
		return subscriberMDN;
	}

	/**
	 * @param subscriberMDN the subscriberMDN to set
	 */
	public void setSubscriberMDN(SubscriberMdn subscriberMDN) {
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
