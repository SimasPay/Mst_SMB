/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.util.List;

import org.hibernate.LockMode;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
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

	public SubscriberMDN getByMDN(String MDN);
	
	/**
	 * Saves the given subscriberMDN record into the databases
	 * @param subscriberMDN
	 */
	public void saveSubscriberMDN(SubscriberMDN subscriberMDN);
	/**
	 * Gets the subscriberMDN from the subscriberMDN table by subscriberMDN ID
	 * @param subscriberMDNId
	 * @return
	 */
	public SubscriberMDN getSubscriberMDNById(Long subscriberMDNId);

	public List<SubscriberMDN> getByQuery(SubscriberMdnQuery query);

	public SubscriberMDN getById(Long id ,LockMode lockMode);
	public SubscriberMDN getById(Long id);
	public int getCountForStatusForMdns(SubscriberMdnQuery query);
	public List<SubscriberMDN> getStatusForMdns(SubscriberMdnQuery query);
 	public Subscriber getSubscriberFromMDN(String MDN);
	public SubscriberMDN getNotRetiredSubscriberMDN(String MDN);
	}
