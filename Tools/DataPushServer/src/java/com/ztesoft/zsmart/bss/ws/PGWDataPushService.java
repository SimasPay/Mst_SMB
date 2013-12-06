/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ztesoft.zsmart.bss.ws;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.datapushserver.Synchronizer;
import com.mfino.util.ConfigurationUtil;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

/**
 *
 * @author admin
 */
@WebService(serviceName = "PGWDataPushInterfaceService", portName = "PGWDataPushInterface", endpointInterface = "com.ztesoft.zsmart.bss.ws.PGWDataPushInterface", targetNamespace = "http://ws.bss.zsmart.ztesoft.com", wsdlLocation = "WEB-INF/wsdl/PGWDataPushService/PGWDataPushInterface.wsdl")

public class PGWDataPushService {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final String errorcode_2 = "2:Failed, Other";
    private final String errorcode__1 = "-1: Authentication Failed. username and password are expected as part of soap headers.";
    private final int Success = 0;
    private final int Failed = 1;

    @Resource
    WebServiceContext context;

    @WebMethod
    public java.lang.String registerNewSubs(java.lang.String msisdn, java.lang.String imsi, java.lang.String marketingCatg, java.lang.String product, java.lang.String firstName, java.lang.String lastName, java.lang.String email, Integer language, java.lang.String currency, java.lang.String paidFlag, java.lang.String idType, java.lang.String idNumber, java.lang.String gender, java.lang.String address, java.lang.String city, java.lang.String birthPlace, javax.xml.datatype.XMLGregorianCalendar birthDate) {
        String returnStr = StringUtils.EMPTY;
        try
        {
            int  isAuthoized = isAuthorized();
            if(isAuthoized != Success)
            {
                return errorcode__1;
            }
            Synchronizer synch = new Synchronizer();
            returnStr = synch.createNewSubscriber(msisdn, firstName, lastName, email,
                    language, currency,
                    paidFlag, birthDate,  idType,
                    idNumber, gender, address, city, birthPlace, imsi,
                    marketingCatg,  product);
        }
        catch(Exception e)
        {
                log.error("Registration of subscriber failed ", e);
                return errorcode_2;
        }
        return returnStr;
    }
    
    @WebMethod
    public java.lang.String updateSubs(java.lang.String msisdn, java.lang.String imsi, java.lang.String marketingCatg, java.lang.String product, java.lang.String firstName, java.lang.String lastName, java.lang.String email, Integer language, java.lang.String currency, java.lang.String paidFlag, java.lang.String idType, java.lang.String idNumber, java.lang.String gender, java.lang.String address, java.lang.String city, java.lang.String birthPlace, javax.xml.datatype.XMLGregorianCalendar birthDate) {
        String returnStr = StringUtils.EMPTY;
        try
        {
            int  isAuthoized = isAuthorized();
            if(isAuthoized != Success)
            {
                return errorcode__1;
            }
			Synchronizer synch = new Synchronizer();
			returnStr = synch.updateSubscriber(msisdn, firstName, lastName,
					email, language, currency, paidFlag, birthDate, idType,
					idNumber, gender, address, city, birthPlace, imsi,
					marketingCatg, product);
        }
        catch(Exception e)
        {
                log.error("Updation of subscriber failed ", e);
                return errorcode_2;
        }
        return returnStr;
    }

   @WebMethod
    public java.lang.String updateRetiredSubs(java.lang.String msisdn) {
        
        String returnStr = StringUtils.EMPTY;
        try
        {
            int  isAuthoized = isAuthorized();
            if(isAuthoized !=Success)
            {
                return errorcode__1;
            }
            Synchronizer synch = new Synchronizer();
            returnStr = synch.updateSubscriberRetiered(msisdn);
        }
        catch(Exception e)
        {
                log.error("Retirement of subscriber failed ", e);
                return errorcode_2;
        }
        return returnStr;
    }

   @WebMethod
    public java.lang.String updateSubsMDN(java.lang.String msisdn, java.lang.String newMSISDN) {
        String returnStr = StringUtils.EMPTY;
        try
        {
            int  isAuthoized = isAuthorized();
            if(isAuthoized !=Success)
            {
                return errorcode__1;
            }
            Synchronizer synch = new Synchronizer();
            returnStr = synch.updateSubscriberMDN(msisdn, newMSISDN);
        }
        catch(Exception e)
        {
                log.error("Registration of subscriber failed ",e);
                return errorcode_2;
        }
        return returnStr;
    }

   @WebMethod
    public java.lang.String lowBalNotif(java.lang.String msisdn, java.lang.String notifType) {
        String returnStr = StringUtils.EMPTY;
        try
        {
            int  isAuthoized = isAuthorized();
            if(isAuthoized != Success)
            {
                return errorcode__1;
            }
            Synchronizer synch = new Synchronizer();
            returnStr = synch.lowBalanceNotif(msisdn, notifType);
        }
        catch(Exception e)
        {
                log.error("Low Balance Notification failed " + e.toString());
                return errorcode_2;
        }
        return returnStr;
    }

   private int isAuthorized()
   {
       try
       {
            MessageContext msgContext = context.getMessageContext();
            HeaderList headerList= (HeaderList)msgContext.get(JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
            if(headerList!=null)
            {
                boolean userNameExists = false;
                boolean passwordExists = false;
                for(int i=0;i<headerList.size();i++)
                {
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
                if(!(userNameExists && passwordExists))
                {
                    return Failed;
                }
            }
            else
            {
                return Failed;
            }
       }
       catch(Exception e)
       {
           log.error("Authentication Failed", e);
           return Failed;
       }
            return Success;
   }
}
