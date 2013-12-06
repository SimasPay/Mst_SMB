package com.mfino.atpp.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Satya
 *
 */
public class MfinoHttpConnector{
	private static final Logger log = LoggerFactory.getLogger(MfinoHttpConnector.class);
	private String timeout;
	

	public String sendHttpRequest(String loginUrl,String xmlRequest) {
		InputStreamReader reader = null;
		StringBuilder buffer = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter("http.connection.timeout", Integer.parseInt(timeout));
		HttpPost httpost = new HttpPost(loginUrl);
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		 nvps.add(new BasicNameValuePair("XML", xmlRequest));
		 try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 HttpResponse response;
		try {
			response = client.execute(httpost);
			HttpEntity resEntity = response.getEntity();	
			log.info("statusline>> " + response.getStatusLine());
			
			
			if (resEntity != null) {
				System.out.println("Response content length: "
						+ resEntity.getContentLength());
				buffer = new StringBuilder();
				reader = new InputStreamReader(response
						.getEntity().getContent());
				
					char[] tmp = new char[1024];
					int l;
					while ((l = reader.read(tmp)) != -1) {
						buffer.append(tmp, 0, l);
					}					
				
			}
			log.info("buffer>>>>>>>>> " + buffer.toString());			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 finally {
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			client.getConnectionManager().shutdown();			
		}	
			
		return buffer.toString(); 
	}
	
	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
}