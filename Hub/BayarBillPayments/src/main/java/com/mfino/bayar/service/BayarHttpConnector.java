package com.mfino.bayar.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
	private Map<String,String> params;
	BayarWebServiceResponse bayarWSResponse = null;
	
	//public BayarWebServiceResponse sendHttpRequest(String method, List<NameValuePair> parameters) throws XmlRpcException, MalformedURLException, SocketTimeoutException {
	public BayarWebServiceResponse sendHttpRequest(String method, Object parameters) throws MalformedURLException, SocketTimeoutException {
				
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
			responseString = "{\"status\":998,\"message\":\"MFS Internal Error\"}";
			log.error(e.getMessage(), e);
			//e.printStackTrace();
		}
		
		bayarWSResponse = BayarWebServiceReponseParser.getBayarWebServiceResponse(responseString);
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

}