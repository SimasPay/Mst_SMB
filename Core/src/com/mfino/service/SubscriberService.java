/**
 * 
 */
package com.mfino.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mfino.dao.query.SubscriberQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.SubscriberSyncRecord;
import com.mfino.domain.User;

/**
 * @author Shashank
 *
 */
public interface SubscriberService {


	public void getLanguages();
	
	/**
	 * calls retireSubscriberAndPockets and changes the status to pendingRetired
	 * @param subscriberMDN
	 * @return
	 */
	public int retireSubscriber(SubscriberMDN subscriberMDN);

	/**
	 * updates status to pendingRetired
	 * @param subscriber
	 */
	void retireSubscriberAndPockets(SubscriberMDN subscriber);

	/**
	 * returns default pocket by mdnId pockettype and commodity
	 * @param subscriberMDNId
	 * @param pocketType
	 * @param commodity
	 * @return
	 */
	public Pocket getDefaultPocket(Long subscriberMDNId, Integer pocketType, Integer commodity);
	/**
	 * does balance enquiry for default pocket
	 * @param subscriberMDNId
	 * @param pocketType
	 * @param commodity
	 * @return
	 */
	public BigDecimal checkBalance(Long subscriberMDNId, Integer pocketType, Integer commodity);

	/**
	 * returb company using brandDao
	 * @param mdn
	 * @return
	 */
	public Company getCompanyFromMDN(String mdn);

	/**
	 * updates mdn with idtype idnumber imsi and marketingCategory
	 * @param mdn
	 * @param subscriberSyncRecord
	 * @return
	 */
	int fillSubscriberMDN(SubscriberMDN mdn, SubscriberSyncRecord subscriberSyncRecord);

	/**
	 * calls createNewSubscriber method to create new subscriber in database
	 * @param subscriberSyncRecord
	 * @return
	 */
	public int createNewSubscriber(SubscriberSyncRecord subscriberSyncRecord);

	/**
	 * creates new subscriber
	 * @param subscriberSyncRecord
	 * @param companyID
	 * @return
	 */
	public int createNewSubscriber(SubscriberSyncRecord subscriberSyncRecord, Long companyID);

	/**
	 * 
	 * @param subscriberSyncRecord
	 * @return
	 */
	public int updateSubscriber(SubscriberSyncRecord subscriberSyncRecord);

	/**
	 * 
	 * @param subscriberSyncRecord
	 * @return
	 */
	public int updateSubscriberRetiered(SubscriberSyncRecord subscriberSyncRecord);

	/**
	 * 
	 * @param subscriberSyncRecord
	 * @param mdn
	 * @return
	 */
	int updatePocket(SubscriberSyncRecord subscriberSyncRecord, SubscriberMDN mdn);

	/**
	 * 
	 * @param language
	 * @return
	 */
	public Integer getLanguage(Integer language);

	/**
	 * 
	 * @param subscriber
	 * @param subscriberSyncRecord
	 * @param isNew
	 * @return
	 */
	int fillSubscriber(Subscriber subscriber, SubscriberSyncRecord subscriberSyncRecord, boolean isNew);

	/**
	 * 
	 * @param pTemplate
	 * @param subMDN
	 */
	void createPocket(PocketTemplate pTemplate, SubscriberMDN subMDN);

	/**
	 * 
	 * @param errorCode
	 * @return
	 */
	public String getErrorMessage(int errorCode);

	/**
	 * 
	 * @param errorCode
	 * @param separator
	 * @return
	 */
	public String getErrorMessage(int errorCode, String separator);
	/**
	 * 
	 * @param mdn
	 * @param pockettype
	 * @param commodity
	 * @return
	 */
	public Pocket getDefaultPocket(String mdn,
			Integer pockettype, Integer commodity);

	/**
	 * 
	 * @param subscriberMDNId
	 * @param templateId
	 * @return
	 */
	public Pocket getDefaultPocket(Long subscriberMDNId, Long templateId);

	
	
	/**
	 * Fetches the e-money pocket for a given subscriber and configuration.
	 * 
	 * @param subscriberMDNId
	 * @param isDefault 
	 * @param isCollectorPocket
	 * @param isSuspensePocket
	 * @return e-money pocket that satisfies the given criteria
	 */
	public Pocket getEmoneyPocket(Long subscriberMDNId, boolean isDefault, boolean isCollectorPocket, boolean isSuspensePocket);

	/**
	 * Saves the Subscriber to the database
	 * @param subscriber
	 */

	public void saveSubscriber(Subscriber subscriber);
	
	/**
	 * Gets the Subscriber from table by the subscriber ID
	 * @param subscriberId
	 * @return
	 */

	public Subscriber getSubscriberbySubscriberId(Long subscriberId);
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public boolean isPasswordExpired(User user);
	
	/**
	 * Normalize mdn with the country code
	 * @param MDN
	 * @return
	 */
	public String normalizeMDN(String MDN);
	
	/**
	 * DeNormalize mdn
	 * @param MDN
	 * @return
	 */
	public String deNormalizeMDN(String MDN);
	
	/**
	 * DeNormalize mdn
	 * @param MDN
	 * @return
	 */
	public List<Subscriber> getByQuery(SubscriberQuery query);
	
	public void verifyEmail(Long subscriberID, String email) throws Exception;

	public Company getDefaultCompanyForSubscriber();
	
	public List<Object[]> getNewSubscribersCount(Date startDate, Date endDate);
}
