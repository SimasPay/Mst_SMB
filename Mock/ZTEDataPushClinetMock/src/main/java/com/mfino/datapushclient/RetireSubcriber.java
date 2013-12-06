package com.mfino.datapushclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceClient;

import ws.datapushserver.mfino.com.DataPushService;
import ws.datapushserver.mfino.com.DataPushService_Service;

import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.developer.WSBindingProvider;

public class RetireSubcriber {
	
	static String webServiceUrl;
	static String webServiceUserName;
	static String webServicePassword;
	
	static String msisdn;
	
	public static void main(String[] args){
		try{
			System.out.println("Started");
			readPropertieFile();
			WebServiceClient ann = DataPushService_Service.class.getAnnotation(WebServiceClient.class);  
			DataPushService_Service service = new DataPushService_Service(new URL(webServiceUrl), new QName(ann.targetNamespace(), ann.name()));
			DataPushService myService = service.getDataPushServicePort();
			WSBindingProvider bp = (WSBindingProvider) myService;
			bp.setOutboundHeaders(Headers.create(new QName("username"),webServiceUserName),Headers.create(new QName("password"),webServicePassword));
			System.out.println("Called web service for the to retire subscriber with msisdn:"+msisdn);
			String result = myService.retireSubs(msisdn);		
			System.out.println("WebService Response :  " + result); 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void readPropertieFile() throws FileNotFoundException, IOException{
		Properties prop = new Properties();
		prop.load(new FileInputStream("retireSubscriber.properties"));
		webServiceUrl = prop.getProperty("webServiceUrl");
		webServiceUserName = prop.getProperty("webServiceUserName");
		webServicePassword = prop.getProperty("webServicePassword");
		
		msisdn = prop.getProperty("msisdn");
	}
}
