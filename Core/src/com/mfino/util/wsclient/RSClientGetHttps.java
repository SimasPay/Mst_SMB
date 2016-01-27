package com.mfino.util.wsclient;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSClientGetHttps {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public 	JSONObject callHttpsGetService(HttpParams prms, String urll, int timeOut) {
		log.info("Entered into callHttpsGetService method of RestFullWebServiceCaller");
		JSONObject result = null;
		JSONObject jErrOut = new JSONObject();
		
		try {
			//instantiates HTTP client to make request
		    HttpClient httpclient = new DefaultHttpClient();
		    httpclient = wrapClient(httpclient);
		    
		    httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeOut);
		    httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeOut);
		    
		    // Setup the request parameters
		    HttpParams params = new BasicHttpParams();
		    params = (BasicHttpParams)prms;
		    
		    //URL with the post data
		    HttpGet httget = new HttpGet(urll);
		    
		    //sets the post request as the resulting string
		    httget.setHeader("Accept", "application/json");
		    httget.addHeader("Content-Type", "application/x-www-form-urlencoded");
		   // httget.setParams(params);
		    
		    //Handles what is returned from the page 
		    System.out.println("Making call to Rest full Webservice");
	        HttpResponse response = httpclient.execute(httget);
	        
	        HttpEntity entity = response.getEntity();
	        String responseStr = EntityUtils.toString(entity);
	        
	        System.out.println("Webservices call is successfull");
	        
	        System.out.println("response is: " + responseStr);
	        
	        jErrOut.put("status", "CommunicationError");
	        
	        if(response != null){
		        //checking the status of the response received
		        int status = response.getStatusLine().getStatusCode();
		        System.out.println("status is: "+status);
		        if (status == 200 || status == 400 || status == 500)
		        {
		        	jErrOut.put("status", "Success");
		        	result = entity != null ? new JSONObject(responseStr) : jErrOut;
		            
		        } else 
		            {
			            result = jErrOut;
		            }
	        }
		} catch (Exception e) {
			System.out.println("Error while calling Restful Web Service: "+e);
			result = jErrOut;
		}
		
		System.out.println(result);
		
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
			System.out.println("connection to https url is successful");
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	 }
	
	public static void main(String[] args) {
		String url = "https://192.168.2.9:8444/KTPMock/validate";

		int timeout = Integer.parseInt("10000");
		RSClientGetHttps smWSCall = new RSClientGetHttps();
		JSONObject jsonout = smWSCall.callHttpsGetService(null,url,timeout);
		System.out.println("cResponse is: "+jsonout);
	}
}
