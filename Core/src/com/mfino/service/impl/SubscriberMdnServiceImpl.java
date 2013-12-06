/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.exceptions.EmptyStringException;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author sunil
 */
@Service("SubscriberMdnServiceImpl")
public class SubscriberMdnServiceImpl implements SubscriberMdnService {

	private Logger log = LoggerFactory.getLogger(SubscriberMdnServiceImpl.class);
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public String getMDNFromSubscriber(Subscriber subscriber) {
		String mdn = null;
		if (subscriber != null) {
			for (SubscriberMDN record : subscriber.getSubscriberMDNFromSubscriberID()) {
				mdn = record.getMDN();
				break;
			}
		}
		return mdn;
	}

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
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public String denormalizeMDN(String mdn) throws EmptyStringException, InvalidMDNException {
		if (StringUtils.isBlank(mdn))
			throw new EmptyStringException("Empty mdn");
		mdn = subscriberService.normalizeMDN(mdn);
//		if (mdn.length()>16)
//			throw new InvalidMDNException("mdn is invalid");
		mdn = mdn.substring(MfinoUtil.countryCode.length());
		return mdn;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public SubscriberMDN getByMDN(String MDN) {
		if(StringUtils.isNotBlank(MDN)){
			SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			return mdnDAO.getByMDN(MDN);
		}
		else{
			return null;
		}
	}
	
	/**
	 * Saves the given subscriberMDN record into the databases
	 * @param subscriberMDN
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void saveSubscriberMDN(SubscriberMDN subscriberMDN){
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		subscriberMDNDAO.save(subscriberMDN);
	}
	
	/**
	 * Gets the subscriberMDN from the subscriberMDN table by subscriberMDN ID
	 * @param subscriberMDNId
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public SubscriberMDN getSubscriberMDNById(Long subscriberMDNId){
		SubscriberMDN subscriberMDN = null;
		if(subscriberMDNId!=null){
			log.info("Getting subscriberMDN with id: "+subscriberMDNId);
			SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			subscriberMDN = subscriberMDNDAO.getById(subscriberMDNId);
		}
		return subscriberMDN;

	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<SubscriberMDN> getByQuery(SubscriberMdnQuery query){

		SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		List<SubscriberMDN> results = mdnDAO.get(query);
		return results;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public SubscriberMDN getById(Long id, LockMode lockMode){

		SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN subscriberMdn = mdnDAO.getById(id,lockMode);
		return subscriberMdn;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public SubscriberMDN getById(Long id){

		SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN subscriberMdn = mdnDAO.getById(id);
		return subscriberMdn;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public int getCountForStatusForMdns(SubscriberMdnQuery query){

		SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		return  mdnDAO.getCountForStatusForMdns(query);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<SubscriberMDN> getStatusForMdns(SubscriberMdnQuery query){

		SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		return  mdnDAO.getStatusForMdns(query);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Subscriber getSubscriberFromMDN(String MDN) {
		if(StringUtils.isNotBlank(MDN)){
			SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			Subscriber subscriber =(mdnDAO.getIDFromMDN(MDN));
			return subscriber;
		}
		else{
			return null;
		}
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public SubscriberMDN getNotRetiredSubscriberMDN(String MDN) {
		SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		return mdnDAO.getByMDNAndNotRetiredStatus(subscriberService.normalizeMDN(MDN));
	}
}
