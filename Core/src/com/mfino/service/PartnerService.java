package com.mfino.service;

import java.util.List;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerServicesDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMPartnerRegistrationThroughAPI;
import com.mfino.mailer.NotificationWrapper;


/**
 * @author Maruthi
 *  partner service.
 */
public interface PartnerService {

	public void getAgentTypes();
	
	public boolean isAgentType(Integer businessPartnerType);

	public Integer getRole(Integer businessPartnerType);
	
	public Partner getPartner(String mdn);
	
	public Partner getPartner(SubscriberMDN mdn);
	
	public Partner getPartnerByPartnerCode(String code);

	public String processActivation(String mdn, String otp) throws MfinoRuntimeException;
	
	public Integer activatePartner(String mdn, String otp);
	
	public void activateServices(Partner partner);
	
	public boolean checkPocket(Pocket pocket,boolean isCollector);

	public void activateNonTransactionable(SubscriberMDN subscriberMDN);
	
	public void retireServices(Partner objPartner);
	
	public void changePin(String username, String transactionPin) throws Exception;
	
	public Partner getPartnerById(Long partnerId);
	
	public List<Partner> get(PartnerQuery query);
	
	public void savePartner(Partner partner);
	
	/**
	 * 
	 * @param partner
	 * @param user
	 * @param type
	 * @param password
	 * @return
	 */
	public String genratePartnerRegistrationMail(Partner partner, User user, Integer type, String password);
	
	/**
	 * 
	 * @param partner
	 * @param oneTimePin
	 * @param mdn
	 * @param notificationMethod
	 * @return
	 */
	public NotificationWrapper genratePartnerOTPMessage(Partner partner, String oneTimePin, String mdn, Integer notificationMethod);

	public List<PartnerServices> getPartnerServices(Long partnerId, Long serviceProviderId, Long serviceId);

	public Partner registerPartner(CMPartnerRegistrationThroughAPI partnerRegistration) throws Exception;

}
