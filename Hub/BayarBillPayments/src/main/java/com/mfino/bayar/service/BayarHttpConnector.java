package com.mfino.bayar.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpsURL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Vishal
 *
 */
public class BayarHttpConnector {

	private static final Logger log = LoggerFactory.getLogger(BayarHttpConnector.class);

	private String timeout;
	private String url;
	private String trustStoreFile;
	private Map<String,String> params;
	BayarWebServiceResponse bayarWSResponse = null;

	//public BayarWebServiceResponse sendHttpRequest(String method, List<NameValuePair> parameters) {
	public BayarWebServiceResponse sendHttpRequest(String method, Object parameters)  {

		log.info("Http client has got the request: "+parameters+" and the loginUrl is: " + url);

		HttpClient httpclient = new DefaultHttpClient();
		if(url.charAt(url.length() - 1) == '/')
		{
			url = url.substring(0, url.length() - 1);
		}
		HttpPost httpPost = new HttpPost(url + "/" + method);
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();

		StringBuilder buffer = new StringBuilder();
		String responseString = null;;

		Iterator<String> it = params.keySet().iterator();
		while(it.hasNext())
		{
			String key = it.next();
			qparams.add(new BasicNameValuePair(key, params.get(key)));
		}

		for(NameValuePair parameter : (List<NameValuePair>)parameters)
		{
			qparams.add(parameter);
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(qparams, HTTP.UTF_8));
			log.info("URI : " + httpPost.getURI()  + " Entity : " + httpPost.getEntity() + " params : " + httpPost.getParams());
			httpclient.getParams().setIntParameter("http.connection.timeout", Integer.parseInt(timeout));
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();

			log.info("statusline>> " + response.getStatusLine());
			if (resEntity != null) {
				log.info("Response content length: "
						+ resEntity.getContentLength());
				buffer = new StringBuilder();
				InputStreamReader reader = new InputStreamReader(response
						.getEntity().getContent());
				try {
					char[] tmp = new char[1024];
					int l;
					while ((l = reader.read(tmp)) != -1) {
						buffer.append(tmp, 0, l);
					}
				} finally {
					reader.close();
				}
			}

			responseString = buffer.toString();
			log.info("responseString: " + responseString);


		} catch (Exception e) {
			responseString = "{\"status\":8888,\"message\":\"MFS Internal Error\"}";
			log.error(e.getMessage(), e);
			//e.printStackTrace();
		}

		bayarWSResponse = BayarWebServiceReponseParser.getBayarWebServiceResponse(responseString);
		return bayarWSResponse;
	}

	
	public BayarWebServiceResponse sendHttpsRequest(String method, Object parameters) {

		

		StringBuilder response = new StringBuilder();
		
		if(url.charAt(url.length() - 1) == '/')
		{
			url = url.substring(0, url.length() - 1);
		}
		
		
		try{
			
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
							throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
							throws CertificateException {
				// TODO Auto-generated method stub

			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		
		StringBuffer urlParameters = new StringBuffer();
		Iterator<String> it = params.keySet().iterator();
		while(it.hasNext())
		{
			String key = it.next();
			urlParameters.append(key + "=" + params.get(key) + "&");
		}
		for(NameValuePair parameter : (List<NameValuePair>)parameters)
		{
			urlParameters.append(parameter.getName() + "=" + parameter.getValue() + "&");
		}
		if(urlParameters.length() > 0){
			urlParameters.deleteCharAt(urlParameters.length() - 1);
		}

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

		URL url = new URL(this.url + "/"+ method);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		log.info("Https client has got the request: "+parameters+" and the loginUrl is: " + url.toString());
		con.setRequestMethod("POST");
		con.setReadTimeout(Integer.parseInt(timeout));
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters.toString());
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();
		}
		catch (Exception e) {
			response = new StringBuilder("{\"status\":8888,\"message\":\"MFS Internal Error\"}");
			log.error(e.getMessage(), e);
			//e.printStackTrace();
		}
		
		bayarWSResponse = BayarWebServiceReponseParser.getBayarWebServiceResponse(response.toString());
		return bayarWSResponse;
	}	

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String,String> getParams() {
		return params;
	}

	public void setParams(Map<String,String> params) {
		this.params = params;
	}
	
	public String getTrustStoreFile() {
		return trustStoreFile;
	}

	public void setTrustStoreFile(String trustStoreFile) {
		this.trustStoreFile = trustStoreFile;
	}


}