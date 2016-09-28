/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.validators;

import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
public class DefaultBOBPocketValidator implements IValidator {

    private SubscriberMDN subscriberMDN;
    private Set<Pocket> pocketSet;
    private Pocket defaultBOBPocket = null; 
    private boolean validateStatus = true;

    public DefaultBOBPocketValidator(SubscriberMDN subscriberMDN) {
        this.subscriberMDN = subscriberMDN;
    }
    
    public DefaultBOBPocketValidator(SubscriberMDN subscriberMDN,boolean validateStatus) {
        this.subscriberMDN = subscriberMDN;
        this.validateStatus = validateStatus;
    }

    public Integer validate() {
        pocketSet = subscriberMDN.getPocketFromMDNID();
        if (pocketSet.size() == 0) {
            return CmFinoFIX.NotificationCode_BOBPocketNotFound;
        }

        for (Pocket pocket : pocketSet) {
            if (pocket.getPocketTemplate() != null) {
                PocketTemplate pTemplate = pocket.getPocketTemplate();
                if (pTemplate.getType().equals(CmFinoFIX.PocketType_BOBAccount) &&
                        pTemplate.getCommodity().equals(CmFinoFIX.Commodity_Airtime) &&BOOL_TRUE.equals(pocket.getIsDefault())) {
                    defaultBOBPocket = pocket;
                    break;
                }
            }
        }
        if (defaultBOBPocket == null) {
            return CmFinoFIX.NotificationCode_BOBPocketNotFound;
        }
        if (validateStatus && !CmFinoFIX.PocketStatus_Active.equals(defaultBOBPocket.getStatus())) {
            return CmFinoFIX.NotificationCode_BOBPocketNotActive;
        }
        if (!CmFinoFIX.SubscriberRestrictions_None.equals(defaultBOBPocket.getRestrictions())) {
            return CmFinoFIX.NotificationCode_BOBPocketIsRestricted;
        }
        if (defaultBOBPocket.getPocketTemplate().getOperatorcode() == null) {
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
	public Pocket getDefaultBOBPocket() {
		return defaultBOBPocket;
	}

	/**
	 * @param defaultSVAPocket the defaultSVAPocket to set
	 */
	public void setDefaultBOBPocket(Pocket defaultBOBPocket) {
		this.defaultBOBPocket = defaultBOBPocket;
	}
}
