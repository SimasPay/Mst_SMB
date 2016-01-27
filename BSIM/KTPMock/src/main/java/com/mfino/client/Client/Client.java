/*package com.mfino.client;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Client {

	public static void main(String[] args) 
	{
		Client cl = new Client();
		cl.callinStreamOutJsonPost();
	}
	
	public void inoutJsonPost()
	{
		String url = "https://localhost:8443/RESTfulExample/rest/payment/inoutJsonPost";
		try{
	    JSONObject inputJsonObj = new JSONObject();
	    inputJsonObj.put("input", "sriWS");
		}catch(Exception exc){
			exc.printStackTrace();
		}
		String rParams = "pocketId=1049";
		int timeout = Integer.parseInt("10000");
		String desc = "Test";
		JSONObject jsonout = callHttpsPostService(rParams,url,timeout,desc);
		System.out.println("cResponse is: "+jsonout);
	}
	
	public void callinStreamOutJsonPost()
	{
		String url = "https://localhost:8443/RESTfulExample/rest/payment/inStreamOutJsonPost";
		try{
			JSONObject verified=new JSONObject();
			verified.put("firstName", "sri");
			verified.put("lastName", "sri");
		
			StringBuffer reqParams = new StringBuffer();
			reqParams.append("phoneNumber=%2b"+"622265390442");
			reqParams.append("&kycStatus=Verified");
			reqParams.append("&verifiedUserData="+verified.toString());
		
			String rParams = reqParams.toString();
			int timeout = Integer.parseInt("10000");
			String desc = "Test";
			JSONObject jsonout = callHttpsPostService(rParams,url,timeout,desc);
			System.out.println("cResponse is: "+jsonout);
		}catch(Exception exc){
			exc.printStackTrace();
			}
	}

	public 	JSONObject callHttpsPostService(String reqParams, String urll, int timeOut, String description) {
		System.out.println("Entered into callHttpsPostService method of RestFullWebServiceCaller");
		String fullResponse = "";
		JSONObject result = null;
		JSONObject jErrOut = new JSONObject();
		System.out.println("class name is: "+this.getClass().getSimpleName());
		
	try {
			System.out.println(description+" Making request with following parameters: "+reqParams);
			System.out.println(description+" Making WS request with following URL: "+urll);
			System.out.println(description+" TimeOut set is: "+timeOut);
			
			jErrOut.put("status", "CommunicationFailure");
			
			//instantiates HTTP client to make request
		    HttpClient httpclient = new DefaultHttpClient();
		    httpclient = wrapClient(httpclient);
		    
		    httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeOut);
		    httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeOut);
		    
		    //URL with the post data
		    HttpPost httpost = new HttpPost(urll);
		    
		    //sets the post request as the resulting string
		    httpost.setHeader("Accept", "application/json");
		    httpost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		    httpost.setEntity(new StringEntity(reqParams));
		    
		    //Handles what is returned from the page 
		    System.out.println(description+" Making call to Rest full Webservice");
	        HttpResponse response = httpclient.execute(httpost);
	        System.out.println(description+" Webservices call is successfull");
	        
	        HttpEntity entity = response.getEntity();
	        fullResponse = EntityUtils.toString(entity);
	        System.out.println(description+" Url full Response in the client class is: "+fullResponse);
	        
	        if(response != null){
		        //checking the status of the response received
		        int status = response.getStatusLine().getStatusCode();
		        System.out.println(description+" Response status is: "+status);
		        if (status == 200 || status == 400 || status == 500) 
		        {
		        	result = entity != null ? new JSONObject(fullResponse) : jErrOut;
					System.out.println("Response sent to Handler");
		        } else 
		            {
			        	System.out.println(description+" Response status is not 200,400,500 - so sending error message to handler");
			        	jErrOut.put("errormsg", fullResponse);
			            result = jErrOut;
		            }
	        }
		} catch (Exception e) {
			System.out.println(description+" Error while calling Restful Web Service: "+e);
			try{
				jErrOut.put("errormsg", fullResponse+"- - "+e);
			}catch(Exception exc){
				System.out.println("Error occured while setting exception to error json object: "+exc);
				}
			
			result = jErrOut;
		}
		return result;
	 }
		
	public HttpClient wrapClient(HttpClient base) {
		try {
			System.out.println("Entered into https wrapClient method");
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}
				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			
			ctx.init(null, new TrustManager[]{tm}, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			System.out.println("Connection to https url is successful");
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();	
			return null;
		}
	 }
	
	
}
*/


//package com.mfino.client;





package com.mfino.client.Client;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Client {

	public static void main(String[] args) 
	{
		Client cl = new Client();
		cl.validate();
				
	}
	
	
	public void validate()
	{
		String url = "https://localhost:8444/KTPMock/validate";
		try{
			JSONObject verified=new JSONObject();
			verified.put("nik", "123456789012");
			verified.put("namalengkap", "Fendy Adidharma Biantoro");
			verified.put("tanggallahir", "1979-10-24");
			verified.put("reffno", "234567890123");
			verified.put("action", "inquiryEKTPPersonal");
			
			StringBuffer reqParams = new StringBuffer();
			reqParams.append("phoneNumber="+"622265390442");
			reqParams.append("&kycStatus=Verified");
			
					
			System.out.println("input request parameters are : "+verified);
			
			String rParams = reqParams.toString();
			int timeout = Integer.parseInt("10000");
			String desc = "Test";			
			JSONObject jsonout = callHttpsPostService(rParams,url,timeout,desc);
			System.out.println("cResponse is: "+jsonout);
		}catch(Exception exc){
			exc.printStackTrace();
			}
	}

	public 	JSONObject callHttpsPostService(String reqParams, String urll, int timeOut, String description) {
		System.out.println("Entered into callHttpsPostService method of RestFullWebServiceCaller");
		String fullResponse = "";
		JSONObject result = null;
		JSONObject jErrOut = new JSONObject();
		System.out.println("class name is: "+this.getClass().getSimpleName());
		
	try {
			System.out.println(description+" Making request with following parameters: "+reqParams);
			System.out.println(description+" Making WS request with following URL: "+urll);
			System.out.println(description+" TimeOut set is: "+timeOut);
			
			jErrOut.put("status", "CommunicationFailure");
			
			//instantiates HTTP client to make request
		    HttpClient httpclient = new DefaultHttpClient();
		    httpclient = wrapClient(httpclient);
		    
		    httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeOut);
		    httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeOut);
		    
		    //URL with the post data
		    HttpPost httpost = new HttpPost(urll);
		    
		    //sets the post request as the resulting string
		    httpost.setHeader("Accept", "application/json");
		    httpost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		    httpost.setEntity(new StringEntity(reqParams));
		    
		    //Handles what is returned from the page 
		    System.out.println(description+" Making call to Rest full Webservice");
	        HttpResponse response = httpclient.execute(httpost);
	        System.out.println(description+" Webservices call is successfull");
	        
	        HttpEntity entity = response.getEntity();	        
	        fullResponse = EntityUtils.toString(entity);
	        System.out.println(description+" Url full Response in the client class is: "+fullResponse);
	        	        
	        
	        if(response != null){
		        //checking the status of the response received
		        int status = response.getStatusLine().getStatusCode();
		        System.out.println(description+" Response status is: "+status);
		        if (status == 200 || status == 400 || status == 500) 
		        {
		        	result = entity != null ? new JSONObject(fullResponse) : jErrOut;
					//System.out.println("Response sent to Handler");
		        } else 
		            {
			        	System.out.println(description+" Response status is not 200,400,500 - so sending error message to handler");
			        	jErrOut.put("errormsg", fullResponse);
			            result = jErrOut;
		            }
	        }
		} catch (Exception e) {
			System.out.println(description+" Error while calling Restful Web Service: "+e);
			try{
				jErrOut.put("errormsg", fullResponse+"- - "+e);
			}catch(Exception exc){
				System.out.println("Error occured while setting exception to error json object: "+exc);
				}
			
			result = jErrOut;
		}
		return result;
	 }
		
	public HttpClient wrapClient(HttpClient base) {
		try {
			System.out.println("Entered into https wrapClient method");
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}
				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			
			ctx.init(null, new TrustManager[]{tm}, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			System.out.println("Connection to https url is successful");
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();	
			return null;
		}
	 }
	
	
}
