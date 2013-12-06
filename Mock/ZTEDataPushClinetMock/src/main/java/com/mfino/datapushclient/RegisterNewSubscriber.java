package com.mfino.datapushclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceClient;

import ws.datapushserver.mfino.com.DataPushService;
import ws.datapushserver.mfino.com.DataPushService_Service;

import com.sun.xml.ws.developer.WSBindingProvider;
import com.sun.xml.ws.api.message.Headers;;
public class RegisterNewSubscriber {
	
	static String webServiceUrl;
	static String webServiceUserName;
	static String webServicePassword;
	
	static String msisdn = "1234567893";
	static String imsi = "12345";
	static String marketCategory = "marketCat";
	static String product = "product";
	static String firstName = "pradeep";
	static String lastName = "kumar";
	static String email = "pradeep@mfino.com";
	static int language = 0;
	static String currency = "rupee";
	static String paidFlag = "yes";
	static String idType = "voterid";
	static String idNumber = "8789091";
	static String gender = "male";
	static String address = "chandanagar";
	static String city = "hyderabad";
	static String birthPlace = "birthplace";
	static int year;
	static int month;
	static int day;
	
	public static void main(String[] args){
		try{
			System.out.println("Started");
			readPropertieFile();
			WebServiceClient ann = DataPushService_Service.class.getAnnotation(WebServiceClient.class);  
			DataPushService_Service service = new DataPushService_Service(new URL(webServiceUrl), new QName(ann.targetNamespace(), ann.name()));
			DataPushService myService = service.getDataPushServicePort();
			WSBindingProvider bp = (WSBindingProvider) myService;
			bp.setOutboundHeaders(Headers.create(new QName("username"),webServiceUserName),Headers.create(new QName("password"),webServicePassword));
		
			
			javax.xml.datatype.XMLGregorianCalendar  birthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
			birthDate.setYear(year);
			birthDate.setMonth(month);
			birthDate.setDay(day);
			
			System.out.println("Called web service to push data of subscriber");
			String result = myService.registerNewSubs(msisdn, imsi, marketCategory, product, firstName, lastName, email, language, currency, paidFlag, idType, idNumber, gender, address, city, birthPlace, birthDate);
			System.out.println("WebService Response : " + result); 

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void readPropertieFile() throws FileNotFoundException, IOException{
		Properties prop = new Properties();
		prop.load(new FileInputStream("newSubscriber.properties"));
		webServiceUrl = prop.getProperty("webServiceUrl");
		webServiceUserName = prop.getProperty("webServiceUserName");
		webServicePassword = prop.getProperty("webServicePassword");
		
		msisdn = prop.getProperty("msisdn");
		imsi = prop.getProperty("imsi");
		marketCategory = prop.getProperty("marketCategory");
		product = prop.getProperty("product");
		firstName = prop.getProperty("firstName");
		lastName = prop.getProperty("lastName");
		email = prop.getProperty("email");
		language = Integer.parseInt(prop.getProperty("language"));
		currency = prop.getProperty("currency");
		paidFlag = prop.getProperty("paidFlag");
		idType = prop.getProperty("idType");
		idNumber = prop.getProperty("idNumber");
		gender = prop.getProperty("gender");
		address = prop.getProperty("address");
		city = prop.getProperty("city");
		birthPlace = prop.getProperty("birthPlace");
		String[] birthDate =  prop.getProperty("birthDate").split("-");
		year = Integer.parseInt(birthDate[0]);
		month = Integer.parseInt(birthDate[1]);
		day = Integer.parseInt(birthDate[2]);
	}

}
