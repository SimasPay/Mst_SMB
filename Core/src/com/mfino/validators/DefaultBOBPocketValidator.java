/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.validators;

import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;

import java.util.Set;

/**
 *
 * @author ADMIN
 */
public class DefaultBOBPocketValidator implements IValidator {

    private SubscriberMdn subscriberMDN;
    private Set<Pocket> pocketSet;
    private Pocket defaultBOBPocket = null; 
    private boolean validateStatus = true;

    public DefaultBOBPocketValidator(SubscriberMdn subscriberMDN) {
        this.subscriberMDN = subscriberMDN;
    }
    
    public DefaultBOBPocketValidator(SubscriberMdn subscriberMDN,boolean validateStatus) {
        this.subscriberMDN = subscriberMDN;
        this.validateStatus = validateStatus;
    }

    public Integer validate() {
        pocketSet = subscriberMDN.getPockets();
        if (pocketSet.size() == 0) {
            return CmFinoFIX.NotificationCode_BOBPocketNotFound;
        }

        
        for (Pocket pocket : pocketSet) {
            if (pocket.getPocketTemplateByPockettemplateid() != null) {
                PocketTemplate pTemplate = pocket.getPocketTemplateByPockettemplateid();
                
                Long tempTypeL = pTemplate.getType().longValue();
				Integer tempTypeLI = tempTypeL.intValue();
				
				Long tempCommodityL = pTemplate.getCommodity().longValue();
				Integer tempCommodityLI = tempCommodityL.intValue();
                
                if (tempTypeLI.equals(CmFinoFIX.PocketType_BOBAccount) &&
                		tempCommodityLI.equals(CmFinoFIX.Commodity_Airtime) &&BOOL_TRUE.equals(pocket.getIsdefault())) {
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
        if (defaultBOBPocket.getPocketTemplateByPockettemplateid().getOperatorcode() == null) {
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
