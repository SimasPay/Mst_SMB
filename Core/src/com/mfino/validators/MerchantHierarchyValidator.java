/**
 * 
 */
package com.mfino.validators;

import com.mfino.domain.Merchant;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Deva
 *
 */

public class MerchantHierarchyValidator implements IValidator {

	private SubscriberMdn childMDN = null;
	
	private SubscriberMdn parentMDN = null;
	
	private Merchant parentMerchant = null;
	
	private Merchant childMerchant = null;
	
	public MerchantHierarchyValidator(SubscriberMdn childMDN, SubscriberMdn parentMDN) {
		this.childMDN = childMDN;
		this.parentMDN = parentMDN;
	}
	
	public MerchantHierarchyValidator(Merchant childMerchant, Merchant parentMerchant) {
		this.childMerchant = childMerchant;
		this.parentMerchant = parentMerchant;
	}
	
	/* (non-Javadoc)
	 * @see com.mfino.validators.IValidator#validate()
	 */
	@Override
	public Integer validate() {
		if(childMerchant == null) {
			childMerchant = loadMerchant(childMDN);
		}
		if (parentMerchant == null) {
			parentMerchant = loadMerchant(parentMDN);
		}
		if (childMerchant == null || parentMerchant == null)
			return CmFinoFIX.NotificationCode_NotDescendentNotification;
		
		if (childMerchant.getMerchantByParentID().getId()!=null && childMerchant.getMerchantByParentID().getId().equals(parentMerchant.getId()))
			return CmFinoFIX.NotificationCode_NotDescendentNotification;
		return CmFinoFIX.ResponseCode_Success;
	}

	/**
	 * @param childMDN2
	 * @return
	 */
	private Merchant loadMerchant(SubscriberMdn mdn) {
		return mdn.getSubscriber().getMerchant();
	}

}
