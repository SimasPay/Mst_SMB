package com.mfino.util.wsclient;

/**
 * @author srinivaas
 *
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class RSClientPostHttp {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public 	JSONObject callHttpPostService(JSONObject jsonObject, String urll, int timeOut) {
		log.info("Entered into callHttpsPostService method of RestFullWebServiceCaller");
		JSONObject result;
		JSONObject jErrOut = new JSONObject();
		try{
			jErrOut.put("status", "CommunicationError");
		}catch(Exception exc){
			exc.printStackTrace();
		}
		try {
			//instantiates HTTP client to make request
		    HttpClient httpclient = new DefaultHttpClient();
		    httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeOut);
		    httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeOut);
		    
		    //URL with the post data
		    HttpPost httpost = new HttpPost(urll);
	
		    //passes the results to a string builder/entity
		    StringEntity se = new StringEntity(jsonObject.toString());
		    
		    //sets the post request as the resulting string
		    httpost.setEntity(se);
		    httpost.setHeader("Accept", "application/json");
		    httpost.setHeader("Content-type", "application/json");
	
		    //Handles what is returned from the page 
		    log.info("Making call to Rest full Webservice");
	        HttpResponse response = httpclient.execute(httpost);
	        log.info("Webservices call is successfull");
	        
	        //checking the status of the response received
	        int status = response.getStatusLine().getStatusCode();
	        if (status == 200) 
	        {
	            HttpEntity entity = response.getEntity();
	            //result = entity != null ? EntityUtils.toString(entity) : jErrOut;
	            result = entity != null ? new JSONObject(EntityUtils.toString(entity)) : jErrOut;
	        } else 
	            {
		            result = jErrOut;
	            }
		} catch (Exception exc) {
			log.info("Error while calling Restful Service: ");
			log.info(""+exc);
			result = jErrOut;
		}
		return result;
	 }

	public static void main(String[] args) {
		try{
			//String url = "http://localhost:8080/RESTfulExample/rest/payment/test";
			//String url = "http://localhost:8080/RESTfulExample/rest/payment/hPay";
			//String url = "http://175.101.5.70:8081/RESTfulExample/rest/payment/hPay";
			String url = "http://182.23.54.69:8080/RESTfulExample/rest/payment/hPay";
			String mdn = "628881851378";
			String emailAddress = "abc.xys@rand-ind.com";
			int timeout = Integer.parseInt("10000");
			//Long timeout = 10000L;
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("mdn", mdn);
			jsonObject.put("emailAddress", emailAddress);
			
			RSClientPostHttp smWSCall = new RSClientPostHttp();
			//String wsResponse = smWSCall.callHttpPostService(jsonObject, url, timeout);
			JSONObject wsResponse = smWSCall.callHttpPostService(jsonObject, url, timeout);
			System.out.println("response received from ws is: "+wsResponse);
	    	//JSONObject jsonout = new JSONObject(wsResponse);
		    String status =  String.valueOf(wsResponse.get("status"));
		    System.out.println("status is: "+status);
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
}