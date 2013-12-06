package com.mfino.ztedatapush;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.ZTEDataPushDAO;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.ZTEDataPush;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.SMSService;
import com.mfino.service.impl.NotificationMessageParserServiceImpl;
import com.mfino.service.impl.SMSServiceImpl;

public class DataPushRecord {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;

	public String insert(String msisdn, String firstName,
			String lastName, String email, Integer language, String currency,
			String paidFlag, XMLGregorianCalendar birthDate, String idType, String idNumber,
			String gender, String address, String city, String birthPlace,
			String imsi, String marketingCatg, String product){

		log.info("DataPushRecord::insert() method called");
		try{
			if(isMsisdnExist(msisdn)){
				return "Subscriber Details already exists corresponding to Msisdn:"+msisdn;
			}
			ZTEDataPush zteDataPush = new ZTEDataPush();
			zteDataPush.setVersion(new Integer(1));
			zteDataPush.setCreateTime(new Timestamp());
			zteDataPush.setLastUpdateTime(new Timestamp());
			zteDataPush.setUpdatedBy("System");
			zteDataPush.setCreatedBy("System");
			zteDataPush.setMsisdn(msisdn);
			zteDataPush.setFirstName(firstName);
			zteDataPush.setLastName(lastName);
			zteDataPush.setEmail(email);
			zteDataPush.setLanguage(language);
			zteDataPush.setCurrency(currency);
			zteDataPush.setPaidFlag(paidFlag);

			Date bDate = null;
			if(birthDate != null &&  birthDate.isValid()){
				bDate = birthDate.toGregorianCalendar().getTime();
			}

			zteDataPush.setBirthDate(new Timestamp(bDate));
			zteDataPush.setIDType(idType);
			zteDataPush.setIDNumber(idNumber);
			zteDataPush.setGender(gender);
			zteDataPush.setAddress(address);
			zteDataPush.setCity(city);
			zteDataPush.setBirthPlace(birthPlace);
			zteDataPush.setIMSI(imsi);
			zteDataPush.setMarketingCatg(marketingCatg);
			zteDataPush.setProduct(product);

			log.info("DataPushRecord::insert(): Trying mfino hibernate Session");
			ZTEDataPushDAO zteDataPushDAO = DAOFactory.getInstance().getZTEDataPushDAO();
			zteDataPushDAO.save(zteDataPush);
			log.info(String.format("DataPushRecord::insert(): Successfully Saved zteDataPush Domain Object(ID:%d)",zteDataPush.getID()));
			log.info(String.format("DataPushRecord::insert(): Called Function to send SMS to Subscriber(msisdn:%s) to register and activate",msisdn));
			sendSMSTOSubscriber(msisdn);
			return "Successfully Saved Subscriber Details";
		}catch(Exception e){
			log.error("Error Occured while saving DataPushRecord", e);
			return "Failed to Save the Subscriber Details. Please try after sometime";
		}

	}

	private boolean isMsisdnExist(String msisdn){
		ZTEDataPushDAO zteDataPushDAO = DAOFactory.getInstance().getZTEDataPushDAO();
		ZTEDataPush zteDataPush = zteDataPushDAO.getByMsisdn(msisdn);
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN subscriberMDN =subscriberMDNDAO.getByMDN(msisdn);
		if(zteDataPush != null){
			log.info(String.format("DataPushRecord::isMsisdnExist(): ZteDataPush Domain Object(ID:%d) already exists corresponding to Msisdn(%s)",zteDataPush.getID(),msisdn));
			return true;
		}else if(subscriberMDN != null){
			log.info(String.format("DataPushRecord::isMsisdnExist(): SubscriberMDN Domain Object(ID:%d) already exists corresponding to Msisdn(%s)",subscriberMDN.getID(),msisdn));
			return true;
		}else{
			log.info(String.format("DataPushRecord::isMsisdnExist(): ZteDataPush Domain Object doesn't exists corresponding to Msisdn(%s)",msisdn));
			return false;
		}
	}
	private void sendSMSTOSubscriber(String msisdn){		
		SMSServiceImpl smsService = new SMSServiceImpl();
		NotificationWrapper notificationWrapper=new NotificationWrapper();

		notificationWrapper.setCode(CmFinoFIX.NotificationCode_SubscriberRegisterAndActivate);
		notificationWrapper.setDestMDN(msisdn);
		smsService.setDestinationMDN(msisdn);
		notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		smsService.setMessage(notificationMessageParserService.buildMessage(notificationWrapper,true));
		smsService.send();
	}

}
