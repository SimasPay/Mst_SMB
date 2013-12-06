/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.datapushserver.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

/**
 *
 * @author admin
 */
@WebService(name = "DataPushService")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public interface DataPushInterface {
   
    @WebMethod
    public java.lang.String registerNewSubs(
    		@WebParam(name = "msisdn")
    		java.lang.String msisdn,
    		@WebParam(name = "imsi")
    		java.lang.String imsi, 
    		@WebParam(name = "marketingCatg")
    		java.lang.String marketingCatg,
    		@WebParam(name = "product")
    		java.lang.String product, 
    		@WebParam(name = "firstName")
    		java.lang.String firstName,
    		@WebParam(name = "lastName")
    		java.lang.String lastName,
    		@WebParam(name = "email")
    		java.lang.String email, 
    		@WebParam(name = "language")
    		Integer language, 
    		@WebParam(name = "currency")
    		java.lang.String currency,
    		@WebParam(name = "paidFlag")
    		java.lang.String paidFlag,
    		@WebParam(name = "idType")
    		java.lang.String idType, 
    		@WebParam(name = "idNumber")
    		java.lang.String idNumber,
    		@WebParam(name = "gender")
    		java.lang.String gender, 
    		@WebParam(name = "address")
    		java.lang.String address, 
    		@WebParam(name = "city")
    		java.lang.String city, 
    		@WebParam(name = "birthPlace")
    		java.lang.String birthPlace,
    		@WebParam(name = "birthDate")
    		javax.xml.datatype.XMLGregorianCalendar birthDate);
    
    @WebMethod
    public java.lang.String retireSubs(
    		@WebParam(name = "msisdn")
    		java.lang.String msisdn);

   @WebMethod
    public java.lang.String suspendSubs(
    		@WebParam(name = "msisdn")
    		java.lang.String msisdn);
   
}
