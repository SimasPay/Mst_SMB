package com.mfino.sterling.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Amar
 *
 */
public class MfinoHttpConnector {

	private static final Logger log = LoggerFactory.getLogger(MfinoHttpConnector.class);

	private String timeout;
	
	public String sendHttpRequest(String url, String xmlRequest) {

		StringBuilder buffer = new StringBuilder();
		String responseString = null;;
		DefaultHttpClient  defaultHttpClient = null;

		try{
			log.info("Http client has got the request: "+xmlRequest+" and the loginUrl is: "+url);


			HttpPost httpost = new HttpPost(url);
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("request", xmlRequest));

			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			// Set verifier
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

			defaultHttpClient = new DefaultHttpClient();
			defaultHttpClient.getParams().setIntParameter("http.connection.timeout", Integer.parseInt(timeout));
			HttpResponse response = defaultHttpClient.execute(httpost);
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
			
			responseString = StringEscapeUtils.unescapeXml(buffer.toString());
			log.info("responseString: " + responseString);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		catch(Exception e){
			log.error("Problem while connecting to the http endpoint "+e.getStackTrace());
		}
		finally{
			defaultHttpClient.getConnectionManager().shutdown();
		}
		return responseString;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

}