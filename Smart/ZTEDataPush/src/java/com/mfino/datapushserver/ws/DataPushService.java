/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.datapushserver.ws;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.HibernateService;
import com.mfino.service.impl.SubscriberServiceImpl;
import com.mfino.util.ConfigurationUtil;
import com.mfino.ztedatapush.DataPushDBSync;
import com.mfino.ztedatapush.DataPushRecord;
import com.mfino.ztedatapush.persistance.MfinoDbHibernateUtil;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

/**
 *
 * @author admin
 */
@WebService(endpointInterface = "com.mfino.datapushserver.ws.DataPushInterface", 
			serviceName = "DataPushService", 
			targetNamespace = "http://com.mfino.datapushserver.ws/")
@BindingType(value = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
public class DataPushService implements DataPushInterface{
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final String errorcode_2 = "2:Failed, Other";
    private final String errorcode__1 = "-1: Authentication Failed. username and password are expected as part of soap headers.";
    private final int Success = 0;
    private final int Failed = 1;
    private Session session = null;
	SubscriberServiceImpl subscriberServiceImpl = new SubscriberServiceImpl();

    @Resource
    WebServiceContext context;

    @Override
    public java.lang.String registerNewSubs(java.lang.String msisdn,java.lang.String imsi, java.lang.String marketingCatg, java.lang.String product, java.lang.String firstName, java.lang.String lastName, java.lang.String email, Integer language, java.lang.String currency, java.lang.String paidFlag, java.lang.String idType, java.lang.String idNumber, java.lang.String gender, java.lang.String address, java.lang.String city, java.lang.String birthPlace, javax.xml.datatype.XMLGregorianCalendar birthDate) {
    	if(!isNewSubDataValid( msisdn, imsi,  marketingCatg,  product,  firstName,  lastName,  email, language,  currency,  paidFlag,  idType,  idNumber,  gender,  address,  city,  birthPlace, birthDate)){
    		return "Registration of subscriber failed for msisdn:"+msisdn+" as some of the data is not valid";
    	}  		  
    	//log.info(String.format("registerNewSubs is called with Parameters[msisdn:%s, imsi:%s, marketingCatg:%s, product:%s, firstName:%s, lastName:%s, email:%s, language:%d, currency:%s, paidFlag:%s, idType:%s, idNumber:%s, gender:%s, address:%s, city:%s, birthPlace:%s, birthDate:%s]",msisdn, imsi, marketingCatg, product, firstName, lastName, email, language, currency, paidFlag, idType, idNumber, gender, address, city, birthPlace, birthDate.toString()));
    	setSession();
    	msisdn = subscriberServiceImpl.normalizeMDN(msisdn);
    	String returnStr = StringUtils.EMPTY;
    	try{
    		log.info("registerNewSubs: isAuthorized function called");
    		int  isAuthoized = isAuthorized();
    		if(isAuthoized != Success){
    			return errorcode__1;
    		}
    		DataPushRecord dataPushRecord = new DataPushRecord();
    		returnStr = dataPushRecord.insert(msisdn, firstName, lastName, email,
    				language, currency,
    				paidFlag, birthDate,  idType,
    				idNumber, gender, address, city, birthPlace, imsi,
    				marketingCatg,  product);
    	}
    	catch(Exception e){
    		log.error("Registration of subscriber failed ", e);
    		return "Registration of subscriber failed for msisdn:"+msisdn;
    	}
    	closeSession();
    	return returnStr;
    }
    
    
   @Override
   public java.lang.String retireSubs(java.lang.String msisdn) {
	   log.info("DataPushService::retireSubs()  is called with msisdn:"+msisdn);
	   setSession();
	   msisdn = subscriberServiceImpl.normalizeMDN(msisdn);
	   String returnStr = StringUtils.EMPTY;
	   try{
		   int  isAuthoized = isAuthorized();
		   if(isAuthoized !=Success)
		   {
			   return errorcode__1;
		   }
		   DataPushDBSync dataPushDbSync = new DataPushDBSync();
		   returnStr = dataPushDbSync.retireSubscriber(msisdn);
	   }catch(Exception e) {
		   log.error("Retirement of subscriber failed ", e);
		   return "Retirement of subscriber failed for msisdn:"+msisdn;
	   }
	   closeSession();
	   return returnStr;
   }

   @Override
    public java.lang.String suspendSubs(java.lang.String msisdn) {
	   log.info("DataPushService::suspendSubs()  is called with msisdn:"+msisdn);
	   setSession();
	   msisdn = subscriberServiceImpl.normalizeMDN(msisdn);
        String returnStr = StringUtils.EMPTY;
        try{
            int  isAuthoized = isAuthorized();
            if(isAuthoized !=Success)
            {
                return errorcode__1;
            }
            DataPushDBSync dataPushDbSync = new DataPushDBSync();
            returnStr = dataPushDbSync.suspendSubscriber(msisdn);
        }catch(Exception e){
                log.error("Suspension of subscriber failed ",e);
                return "Suspension of subscriber failed for msisdn:"+msisdn;
        }
        closeSession();
        return returnStr;
    }

  

   private int isAuthorized()
   {
	   try{
		   MessageContext msgContext = context.getMessageContext();
		   HeaderList headerList= (HeaderList)msgContext.get(JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
		   if(headerList!=null){
			   boolean userNameExists = false;
			   boolean passwordExists = false;
			   for(int i=0;i<headerList.size();i++){
				   if(headerList.get(i).getLocalPart().equals("username") &&
						   headerList.get(i).getStringContent().equals(ConfigurationUtil.getDataPushUserName()))
				   {
					   userNameExists = true;
				   }
				   if(headerList.get(i).getLocalPart().equals("password") &&
						   headerList.get(i).getStringContent().equals(ConfigurationUtil.getDataPushPassword()))
				   {
					   passwordExists = true;
				   }
			   }
			   if(!(userNameExists && passwordExists)){
				   return Failed;
			   }
		   }else{
			   return Failed;
		   }
	   }
	   catch(Exception e){
		   log.error("Authentication Failed", e);
		   return Failed;
	   }
	   return Success;
   }
   
   private void setSession(){
	   session = MfinoDbHibernateUtil.getSessionFactory().openSession();
	   HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
	   HibernateSessionHolder sessionHolder = hibernateService.getHibernateSessionHolder();
	   sessionHolder.setSession(session);
	   DAOFactory.getInstance().setHibernateSessionHolder(sessionHolder);
   }
   
   private void closeSession(){
	   session.close();
   }
   
   private boolean isNewSubDataValid(java.lang.String msisdn,java.lang.String imsi, java.lang.String marketingCatg, java.lang.String product, java.lang.String firstName, java.lang.String lastName, java.lang.String email, Integer language, java.lang.String currency, java.lang.String paidFlag, java.lang.String idType, java.lang.String idNumber, java.lang.String gender, java.lang.String address, java.lang.String city, java.lang.String birthPlace, javax.xml.datatype.XMLGregorianCalendar birthDate){
	   if(StringUtils.isBlank(msisdn)|| StringUtils.isBlank(imsi)|| StringUtils.isBlank(marketingCatg)
               || StringUtils.isBlank(product) || StringUtils.isBlank(firstName)|| StringUtils.isBlank(lastName)
               || StringUtils.isBlank(lastName)|| StringUtils.isBlank(paidFlag)){
		   log.info("DataPushService::isNewSubDataValid()  some of the required fields are missing for new subscriber with msisdn:"+msisdn);
		   return false;
	   }else{
		   return true;
	   }
	   
   }
}
