/**
 * 
 */
package com.mfino.validators;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Partner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.BillerService;
import com.mfino.service.PartnerService;

/**
 * @author Deva
 * 
 * For Now we need only Source merchant validation. So just adding Source merchant validator
 *
 */

public class PartnerValidator implements IValidator {

	private SubscriberMdn subscriberMDN;

	private String mdn;

	private String code;

	private Partner partner;

	private boolean isAgent;
	
	private boolean isTeller;

	private boolean isMerchant;

	private boolean isBiller;

	private String billerCode;
	
	@Autowired
	@Qualifier("BillerServiceImpl")
	private BillerService billerService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;

	/**
	 * 
	 */
	public PartnerValidator(){

	}

	public PartnerValidator(SubscriberMdn subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}
	public PartnerValidator(String mdn) {
		this.mdn = mdn;
	}
	/* (non-Javadoc)
	 * @see com.mfino.validators.IValidator#validate()
	 */
	//TODO:: Add Partner validation based on type, only need to return true if 
	//for the partner code the partner's type matches with the type provided.
	@Override
	public Integer validate() {
		if (subscriberMDN == null) {
			SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			if(mdn!=null){
				subscriberMDN = subscriberMDNDAO.getByMDN(mdn);
				if (subscriberMDN == null) {
					return CmFinoFIX.NotificationCode_PartnerNotFound;
				}
				Set<Partner> partnerSet = subscriberMDN.getSubscriber().getPartners();
				if(partnerSet==null||partnerSet.isEmpty())
					return CmFinoFIX.NotificationCode_PartnerNotFound;
				partner = partnerSet.iterator().next();
			}else if(code!=null){
				PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
				partner=partnerDAO.getPartnerByPartnerCode(code);
			}else if(billerCode!=null){
				partner = billerService.getPartner(billerCode);
			}
			if(partner!=null){
				subscriberMDN=partner.getSubscriber().getSubscriberMdns().iterator().next();
			} else {
				if(isAgent)
					return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
				return CmFinoFIX.NotificationCode_PartnerNotFound;
			}
			if (subscriberMDN == null) {
				return CmFinoFIX.NotificationCode_MDNNotFound;
			}
		}
		Subscriber agentsubscriber=subscriberMDN.getSubscriber();

		Long tempTypeL = agentsubscriber.getType();
		Integer tempTypeLI = tempTypeL.intValue();
		
		if(!(tempTypeLI.equals(CmFinoFIX.SubscriberType_Partner))){
			if(isAgent)
				return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
			return CmFinoFIX.NotificationCode_PartnerNotFound;//agent Not found			
		}
		if(partner==null){
			Set<Partner> agentPartner = agentsubscriber.getPartners();
			if(!agentPartner.isEmpty()){
				partner=agentPartner.iterator().next();
			}

		}
		if(partner==null){
			if(isAgent)
				return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
			return CmFinoFIX.NotificationCode_PartnerNotFound;//agent Not found			
		}
		if(isAgent && !partnerService.isAgentType(partner.getBusinesspartnertype().intValue()) && !(partner.getBusinesspartnertype().equals(CmFinoFIX.BusinessPartnerType_BranchOffice))){
			//return CmFinoFIX.NotificationCode_PartnerNotFound;//agent Not found
			return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
		}
		if(isTeller && !(partner.getBusinesspartnertype().equals(CmFinoFIX.BusinessPartnerType_BranchOffice))){
			return CmFinoFIX.NotificationCode_PartnerNotFound;//teller Not found
		}
		if(isMerchant && !(partner.getBusinesspartnertype().equals(CmFinoFIX.BusinessPartnerType_Merchant))){
			return CmFinoFIX.NotificationCode_PartnerNotFound;//Merchant Not found
		}
		if (!(CmFinoFIX.SubscriberStatus_Active.equals(agentsubscriber.getStatus())&&CmFinoFIX.SubscriberStatus_Active.equals(subscriberMDN.getStatus()))) {
			return CmFinoFIX.NotificationCode_MDNIsNotActive;
		}
		if (!(CmFinoFIX.SubscriberRestrictions_None.equals(agentsubscriber.getRestrictions())&&CmFinoFIX.SubscriberRestrictions_None.equals(subscriberMDN.getRestrictions()))) {
			return CmFinoFIX.NotificationCode_MDNIsRestricted;
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
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return code;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	/**
	 * @return the isAgent
	 */
	public boolean isAgent() {
		return isAgent;
	}

	/**
	 * @param isAgent the isAgent to set
	 */
	public void setIsAgent(boolean isAgent) {
		this.isAgent = isAgent;
	}

	/**
	 * @return the isMerchant
	 */
	public boolean isMerchant() {
		return isMerchant;
	}

	/**
	 * @param isMerchant the isMerchant to set
	 */
	public void setIsMerchant(boolean isMerchant) {
		this.isMerchant = isMerchant;
	}

	/**
	 * @return the isBiller
	 */
	public boolean isBiller() {
		return isBiller;
	}

	/**
	 * @param isBiller the isBiller to set
	 */
	public void setIsBiller(boolean isBiller) {
		this.isBiller = isBiller;
	}
	
	public void setBillerCode(String billerCode) {
		this.billerCode = billerCode;
	}
	
	public String getBillerCode() {
		return billerCode;
	}

	public boolean isTeller() {
		return isTeller;
	}

	public void setIsTeller(boolean isTeller) {
		this.isTeller = isTeller;
	}
}
