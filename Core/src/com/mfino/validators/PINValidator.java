/**
 * 
 */
package com.mfino.validators;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Partner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.MfinoUtil;

/**
 * @author Deva
 * 
 */
public class PINValidator implements IValidator {

	private SubscriberMdn	subscriberMDN;
	private Subscriber		subscriber;

	private String	      mdn;

	private String	      pin;

	private String	      newPinDigest;
	private XMLResult	  result;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;

	public PINValidator(SubscriberMdn subscriberMDN, String pin) {
		this.subscriberMDN = subscriberMDN;
		this.subscriber = subscriberMDN.getSubscriber();
		this.pin = pin;
	}

	public PINValidator(SubscriberMdn subscriberMDN, String pin, XMLResult result) {
		this.subscriberMDN = subscriberMDN;
		this.subscriber = subscriberMDN.getSubscriber();
		this.pin = pin;
		this.result = result;
	}
	
	/**
	 * @param sourceMDN
	 * @param pin2
	 */
	public PINValidator(String sourceMDN, String pin2) {
		this.mdn = sourceMDN;
		this.pin = pin2;
	}
	
	public PINValidator(String sourceMDN, String pin2, XMLResult result) {
		this.mdn = sourceMDN;
		this.pin = pin2;
		this.result = result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfino.validators.Validator#validate()
	 */
	@Override
	public Integer validate() {
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
		if (subscriberMDN == null) {
			subscriberMDN = subscriberMDNDAO.getByMDN(mdn);
			if (subscriberMDN == null) {
				return CmFinoFIX.NotificationCode_MDNNotFound;
			}
		}
		subscriber = subscriberMDN.getSubscriber();
		if (this.pin == null) { // || pin.length() < ConfigurationUtil.getMinPINLength()
			return CmFinoFIX.NotificationCode_InvalidSMSCommand;
		}
		//		if ( isSubscriberActivity && subscriberMDN.getDigestedPIN() == null) {
		//			return CmFinoFIX.NotificationCode_PINResetRequired;
		//		} else if (!isSubscriberActivity  && (subscriberMDN.getMerchantDigestedPIN() == null && subscriberMDN.getDigestedPIN() == null)) {
		//			return CmFinoFIX.NotificationCode_PINResetRequired;
		//		}
		// we will not check length when the pin check happens, because PIN length might have been changed in config file
		// after the PIN has been set by the subscriber. We take care of PIN length in case of changing PIN.
		
		String calcPIN = MfinoUtil.calculateDigestPin(subscriberMDN.getMdn(), pin);
		String digestedPin = null;

		// If merchant and the merchant pin exists use it otherwise use the regular pin
		//			if ( !isSubscriberActivity){
		//				digestedPin = subscriberMDN.getMerchantDigestedPIN();	
		//				if(StringUtils.isBlank(digestedPin))
		//					digestedPin = subscriberMDN.getDigestedPIN();
		//			} else {
		digestedPin = subscriberMDN.getDigestedpin();
		//			}

		if(StringUtils.isBlank(digestedPin)){
			return CmFinoFIX.NotificationCode_PINResetRequired;
		}
		if (!calcPIN.equalsIgnoreCase(digestedPin)) {
			Long erPinCount = subscriberMDN.getWrongpincount();
			
			int wrongPINCount = erPinCount.intValue();
			subscriberMDN.setWrongpincount(wrongPINCount + 1);
			if (result != null) {
				result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - erPinCount.intValue());
			}
			recalculateMDNRestrictions();
			subscriberMDNDAO.save(subscriberMDN);
			subscriberDAO.save(subscriber);
			return CmFinoFIX.NotificationCode_WrongPINSpecified;
		}
		else {
			// reset wrong pin count and allow them to login
			if (subscriberMDN.getWrongpincount() > 0) {
				subscriberMDN.setWrongpincount(0);
				subscriberMDNDAO.save(subscriberMDN);
				newPinDigest = calcPIN;
				return CmFinoFIX.ResponseCode_Success;
			}
		}
		
		return CmFinoFIX.ResponseCode_Success;
	}

	/**
	 * 
	 */
	private void recalculateMDNRestrictions() {
		if ((subscriberMDN.getRestrictions() & CmFinoFIX.SubscriberRestrictions_SecurityLocked) != 0) {
			return;
		}
		if (subscriberMDN.getWrongpincount() >= systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT)) {
			Timestamp now = new Timestamp();
			subscriberMDN.setRestrictions(subscriberMDN.getRestrictions() | CmFinoFIX.SubscriberRestrictions_SecurityLocked);
			subscriberMDN .setStatus(CmFinoFIX.SubscriberStatus_InActive);
			subscriberMDN.setStatustime(now);
			subscriber.setRestrictions(subscriber.getRestrictions() | CmFinoFIX.SubscriberRestrictions_SecurityLocked);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
			subscriber.setStatustime(now);
			subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
			
			// Check if the Subscriber is of Partner type
			if (CmFinoFIX.SubscriberType_Partner.equals(subscriber.getType())) {
				Set<Partner> setPartners = subscriber.getPartners();
				if (CollectionUtils.isNotEmpty(setPartners)) {
					PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
					Partner partner = setPartners.iterator().next();
					partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_InActive);
					partnerDAO.save(partner);
				}
			}
		}
	}

	public String getNewPinDigest() {
		return newPinDigest;
	}

	public void setNewPinDigest(String newPinDigest) {
		this.newPinDigest = newPinDigest;
	}
}
