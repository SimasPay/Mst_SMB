/**
 * 
 */
package com.mfino.validators;

import java.util.Set;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Deva
 * 
 */
public class DefaultSVAPocketValidator implements IValidator {

	private SubscriberMDN subscriberMDN;

	private String mdn;

	private Set<Pocket> pocketSet;

	private Pocket defaultSVAPocket = null;

	public DefaultSVAPocketValidator(SubscriberMDN subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}

	public DefaultSVAPocketValidator(String mdn) {
		this.mdn = mdn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.validators.Validator#validate()
	 */
	@Override
	public Integer validate() {
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		if (subscriberMDN == null) {
			subscriberMDN = subscriberMDNDAO.getByMDN(mdn);
			if (subscriberMDN == null) {
				return CmFinoFIX.NotificationCode_MDNNotFound;
			}
		}
		pocketSet = subscriberMDN.getPocketFromMDNID();
		if (pocketSet.size() == 0) {
			return CmFinoFIX.NotificationCode_NoSVAPocketsWereFound;
		}

		for (Pocket pocket : pocketSet) {
			if (pocket.getPocketTemplate() != null) {
				PocketTemplate pTemplate = pocket.getPocketTemplate();
				if (pTemplate.getType().equals(CmFinoFIX.PocketType_SVA)
						&& pTemplate.getCommodity().equals(
								CmFinoFIX.Commodity_Airtime) &&BOOL_TRUE.equals(pocket.getIsDefault())) {
					defaultSVAPocket = pocket;
					break;
				}
			}
		}
		if (defaultSVAPocket == null) {
			return CmFinoFIX.NotificationCode_NoSVAPocketsWereFound;
		}
		if( !CmFinoFIX.PocketStatus_Active.equals(defaultSVAPocket.getStatus()) )
		{
			return CmFinoFIX.NotificationCode_SenderAirTimeSVAPocketNotActive;
		}
		if( !CmFinoFIX.SubscriberRestrictions_None.equals(defaultSVAPocket.getRestrictions()) )
		{
			return CmFinoFIX.NotificationCode_SenderSVAPocketRestricted;
		}
		if (defaultSVAPocket.getPocketTemplate().getOperatorcode() == null) {
			return CmFinoFIX.NotificationCode_PocketTemplateOperatorCodeMissing;
		}
		return CmFinoFIX.ResponseCode_Success;
	}

	/**
	 * @return the pocketSet
	 */
	public Set<Pocket> getPocketSet() {
		return pocketSet;
	}

	/**
	 * @param pocketSet the pocketSet to set
	 */
	public void setPocketSet(Set<Pocket> pocketSet) {
		this.pocketSet = pocketSet;
	}

	/**
	 * @return the defaultSVAPocket
	 */
	public Pocket getDefaultSVAPocket() {
		return defaultSVAPocket;
	}

	/**
	 * @param defaultSVAPocket the defaultSVAPocket to set
	 */
	public void setDefaultSVAPocket(Pocket defaultSVAPocket) {
		this.defaultSVAPocket = defaultSVAPocket;
	}

}
