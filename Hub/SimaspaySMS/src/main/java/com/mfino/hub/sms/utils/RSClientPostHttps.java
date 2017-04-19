package com.mfino.hub.sms.utils;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSClientPostHttps {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public 	JSONObject callHttpsPostService(String reqParams, String urll, int timeOut, String description) {
		log.info("Entered into callHttpsPostService method of RestFullWebServiceCaller");
		String fullResponse = "";
		JSONObject result = null;
		JSONObject jErrOut = new JSONObject();
		
	try {
			log.info(description+" Making request with following parameters: "+reqParams);
			log.info(description+" Making WS request with following URL: "+urll);
			log.info(description+" TimeOut set is: "+timeOut);
			
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
		    httpost.addHeader("Content-Type", "application/json");
		    
		    if(null != reqParams) {
		    	
		    	httpost.setEntity(new StringEntity(reqParams));
		    }
		    
		    //Handles what is returned from the page 
		    log.info(description+" Making call to Rest full Webservice");
	        HttpResponse response = httpclient.execute(httpost);
	        log.info(description+" Webservices call is successfull");
	        
	        HttpEntity entity = response.getEntity();
	        fullResponse = EntityUtils.toString(entity);
	        log.info(description+" Url full Response in the client class is: "+fullResponse);
	        
	        if(response != null){
		        //checking the status of the response received
		        int status = response.getStatusLine().getStatusCode();
		        log.info(description+" Response status is: "+status);
		        if (status == 200 || status == 400 || status == 500) 
		        {
		        	result = entity != null ? new JSONObject(fullResponse) : jErrOut;
					log.info("Response sent to Handler");
		        } else 
		            {
			        	log.info(description+" Response status is not 200,400,500 - so sending error message to handler");
			        	jErrOut.put("errormsg", fullResponse);
			            result = jErrOut;
		            }
	        }
		} catch (Exception e) {
			log.info(description+" Error while calling Restful Web Service: "+e);
			try{
				jErrOut.put("errormsg", fullResponse+"- - "+e);
			}catch(Exception exc){
				log.info("Error occured while setting exception to error json object: "+exc);
				}
			
			result = jErrOut;
		}
		return result;
	 }
		
	public HttpClient wrapClient(HttpClient base) {
		try {
			log.info("Entered into https wrapClient method");
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
			log.info("connection to https url is successful");
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			log.info("Error occured during establishing secure connection ",ex);
			return null;
		}
	 }
}
