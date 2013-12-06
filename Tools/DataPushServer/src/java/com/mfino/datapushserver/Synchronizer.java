/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.datapushserver;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.GeneralConstants;
import com.mfino.domain.SubscriberSyncRecord;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.SubscriberService;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author admin
 */
public class Synchronizer {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     *  starts the hibernate session
     */
    private void startProcess()
    {
            HibernateUtil.getCurrentSession().beginTransaction();
    }
    
    /**
     *  commits or rollback the hibernate session and return the error message
     */
    private String endProcess(int errorCode)
    {
        if(errorCode == CmFinoFIX.SynchError_Success || errorCode == CmFinoFIX.SynchError_Failed_Subscriber_is_registered_as_an_active_merchant)
        {
            HibernateUtil.getCurrentTransaction().commit();
        }
        else
        {
              HibernateUtil.getCurrentTransaction().rollback();
        }
        return SubscriberService.getErrorMessage(errorCode, GeneralConstants.COLON_STRING);
    }
    
	/**
	 * creates new subscriber and saves it.
	 * 
	 * @param msisdn
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param language
	 * @param currency
	 * @param paidFlag
	 * @param bDate
	 * @param idType
	 * @param idNumber
	 * @param gender
	 * @param address
	 * @param city
	 * @param birthPlace
	 * @param imsi
	 * @param marketingCatg
	 * @param product
	 * @return
	 */
	public String createNewSubscriber(String msisdn, String firstName,
			String lastName, String email, Integer language, String currency,
			String paidFlag, XMLGregorianCalendar birthDate, String idType, String idNumber,
			String gender, String address, String city, String birthPlace,
			String imsi, String marketingCatg, String product) {
		startProcess();
		if(StringUtils.isBlank(msisdn)|| StringUtils.isBlank(imsi)|| StringUtils.isBlank(marketingCatg)
                || StringUtils.isBlank(product) || StringUtils.isBlank(firstName)|| StringUtils.isBlank(lastName)
                || StringUtils.isBlank(lastName)|| StringUtils.isBlank(paidFlag)
                )
        {
            log.info("Registration of subscriber failed, Missing mandatory fields - " + msisdn);
            return SubscriberService.getErrorMessage(CmFinoFIX.SynchError_Missing_Mandatory_Fields, GeneralConstants.COLON_STRING);
        }
		Date bDate = null;
         if(birthDate != null &&  birthDate.isValid())
         {
             bDate = birthDate.toGregorianCalendar().getTime();
         }
         SubscriberSyncRecord subscriberSyncRecord = new SubscriberSyncRecord(msisdn, firstName, lastName, email,
             language, currency,
             paidFlag, bDate,  idType,
             idNumber, gender, address, city, birthPlace, imsi,
             marketingCatg,  product);
         log.info("createNewSubscriber " + subscriberSyncRecord.Serialize());
         SubscriberService subscriberService = new SubscriberService();
         int errorCode = subscriberService.createNewSubscriber(subscriberSyncRecord);
		return endProcess(errorCode);
	}
	/**
	 * updates the info of existing subscriber
	 * 
	 * @param msisdn
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param language
	 * @param currency
	 * @param paidFlag
	 * @param birthDate
	 * @param idType
	 * @param idNumber
	 * @param gender
	 * @param address
	 * @param city
	 * @param birthPlace
	 * @param imsi
	 * @param marketingCatg
	 * @param product
	 * @return
	 */
	public String updateSubscriber(String msisdn, String firstName,
			String lastName, String email, Integer language, String currency,
			String paidFlag, XMLGregorianCalendar birthDate, String idType,
			String idNumber, String gender, String address, String city,
			String birthPlace, String imsi, String marketingCatg, String product) {
		startProcess();
		if(StringUtils.isBlank(msisdn))
        {
			log.error("Updation of subscriber failed, Missing mandatory fields - " + msisdn);
            return SubscriberService.getErrorMessage(CmFinoFIX.SynchError_Missing_Mandatory_Fields, GeneralConstants.COLON_STRING);
        }
		Date bDate = null;
        if(birthDate != null &&  birthDate.isValid())
        {
             bDate = birthDate.toGregorianCalendar().getTime();
        }
        SubscriberSyncRecord subscriberSyncRecord = new SubscriberSyncRecord(msisdn, firstName, lastName, email,
             language, currency,
             paidFlag, bDate,  idType,
             idNumber, gender, address, city, birthPlace, imsi,
             marketingCatg,  product);
         log.info("updateSubscriber " + subscriberSyncRecord.Serialize());
        SubscriberService subscriberService = new SubscriberService();
        int errorCode = subscriberService.updateSubscriber(subscriberSyncRecord);
		return endProcess(errorCode);
	}
	
	/**
	 * Retires the subscriber
	 * @param msisdn
	 * @return
	 */
	public String updateSubscriberRetiered(String msisdn) {
		if(StringUtils.isBlank(msisdn))
        {
			log.info("Updation of subscriber failed, Missing mandatory fields - " + msisdn);
            return SubscriberService.getErrorMessage(CmFinoFIX.SynchError_Missing_Mandatory_Fields, GeneralConstants.COLON_STRING);
        }
		startProcess();
		SubscriberSyncRecord subscriberSyncRecord = new SubscriberSyncRecord(msisdn);
         log.info("updateSubscriberRetiered " + subscriberSyncRecord.Serialize());
		SubscriberService subscriberService = new SubscriberService();
        int errorCode = subscriberService.updateSubscriberRetiered(subscriberSyncRecord);
		return endProcess(errorCode);
	}
	
	/**
	 * Sets low balance notification
	 * @param msisdn
	 * @return
	 */
	public String lowBalanceNotif(String msisdn, String notifType) {
		if(StringUtils.isBlank(msisdn)||StringUtils.isBlank(notifType))
        {
			log.info("Updation of subscriber failed, Missing mandatory fields - " + msisdn);
            return SubscriberService.getErrorMessage(CmFinoFIX.SynchError_Missing_Mandatory_Fields, GeneralConstants.COLON_STRING);
        }
		startProcess();
		SubscriberSyncRecord subscriberSyncRecord = new SubscriberSyncRecord(msisdn);
         log.info("lowBalanceNotif " + subscriberSyncRecord.Serialize() + " notifType " + notifType);
		SubscriberService subscriberService = new SubscriberService();
        int errorCode = subscriberService.lowBalanceNotif(subscriberSyncRecord, notifType);
		return endProcess(errorCode);
	}
	
	/**
	 * Updates subscriber MDN if the mdn already exists.
	 * 
	 * @param msisdn
	 * @param newMSISDN
	 * @return
	 */
	public String updateSubscriberMDN(String msisdn, String newMSISDN) {
		if(StringUtils.isBlank(msisdn)|| StringUtils.isBlank(newMSISDN))
        {
			log.info("Updation of subscriber failed, Missing mandatory fields - " + msisdn);
            return SubscriberService.getErrorMessage(CmFinoFIX.SynchError_Missing_Mandatory_Fields, GeneralConstants.COLON_STRING);
        }
		startProcess();
		SubscriberSyncRecord subscriberSyncRecord = new SubscriberSyncRecord(msisdn,newMSISDN);
         log.info("updateSubscriberMDN " + subscriberSyncRecord.Serialize());
		SubscriberService subscriberService = new SubscriberService();
        int errorCode = subscriberService.updateSubscriberMDN(subscriberSyncRecord);
		return endProcess(errorCode);
	}
}
