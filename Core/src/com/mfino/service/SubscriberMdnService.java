/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.util.List;

import org.hibernate.LockMode;

import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.exceptions.EmptyStringException;
import com.mfino.exceptions.InvalidMDNException;

/**
 * 
 * @author Vishal
 */
public interface SubscriberMdnService {

	public String getMDNFromSubscriber(Subscriber subscriber);

	/**
	 * Removes the countrycode of the mdn.<br>
	 * Country code is assumed to be the first two digits of mdn after
	 * normalization. <br>
	 * Throws EmptyStringException if StringUtils.isBlank(mdn)<br>
	 * Throws InvalidMDNException if the normalized mdn length is not 12 or 14
	 * 
	 * @param mdn
	 * @return mdn without countrycode
	 * @throws EmptyStringException
	 */
	public String denormalizeMDN(String mdn) throws EmptyStringException, InvalidMDNException;

	public SubscriberMdn getByMDN(String MDN);
	
	/**
	 * Saves the given subscriberMDN record into the databases
	 * @param subscriberMDN
	 */
	public void saveSubscriberMDN(SubscriberMdn subscriberMDN);
	/**
	 * Gets the subscriberMDN from the subscriberMDN table by subscriberMDN ID
	 * @param subscriberMDNId
	 * @return
	 */
	public SubscriberMdn getSubscriberMDNById(Long subscriberMDNId);

	public List<SubscriberMdn> getByQuery(SubscriberMdnQuery query);

	public SubscriberMdn getById(Long id ,LockMode lockMode);
	public SubscriberMdn getById(Long id);
	public int getCountForStatusForMdns(SubscriberMdnQuery query);
	public List<SubscriberMdn> getStatusForMdns(SubscriberMdnQuery query);
 	public Subscriber getSubscriberFromMDN(String MDN);
	public SubscriberMdn getNotRetiredSubscriberMDN(String MDN);

	public void save(SubscriberMdn subscriberMdn);
	}
