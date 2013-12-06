package com.mfino.service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MfinoHttpConnector {
	
	private static final Logger log = LoggerFactory.getLogger(MfinoHttpConnector.class);
	
	private String trustStoreFile;
	private String httpsPort;
	private String timeout;
	
	public String sendHttpRequest(String loginUrl,String xmlRequest) {
		StringBuilder buffer = new StringBuilder();
		DefaultHttpClient client = null;
		DefaultHttpClient httpClient = null;
		try {
			log.info("Http client has got the request: "+xmlRequest+" and the loginUrl is: "+loginUrl);
			
			SSLContext sslContext = SSLContext.getInstance("TLS");   
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		    KeyStore ks = KeyStore.getInstance("JKS");
		    File trustFile = new File(trustStoreFile);
		    ks.load(new FileInputStream(trustFile), null);
		    tmf.init(ks);
		    sslContext.init(null, tmf.getTrustManagers(),null);  
		    SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext);
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			client = new DefaultHttpClient();
			client.getParams().setIntParameter("http.connection.timeout", Integer.parseInt(timeout));
			
			SchemeRegistry registry = new SchemeRegistry();
			socketFactory
					.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			registry.register(new Scheme("https", socketFactory, Integer.parseInt(httpsPort)));
			SingleClientConnManager mgr = new SingleClientConnManager(
					client.getParams(), registry);
			httpClient = new DefaultHttpClient(mgr,
					client.getParams());

			// Set verifier
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

			HttpPost httpost = new HttpPost(loginUrl);
			StringEntity se = new StringEntity(xmlRequest);
			se.setContentType("text/xml; charset=utf-8");
			httpost.setEntity(se);

			HttpResponse response = httpClient.execute(httpost);
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

			log.info("buffer>>>>>>>>> " + buffer.toString());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		catch (NoSuchAlgorithmException e) {
			log.error("Alogorithm not available in the Environment"+e.getStackTrace());
		}
		catch (KeyStoreException e) {
			log.error("Problem while accessing the store"+e.getStackTrace());
		}
		catch (CertificateException e) {
			log.error("Problem validating the Certificate"+e.getStackTrace());
		}
		catch (KeyManagementException e) {
			log.error("Problem validating the keys"+e.getStackTrace());
		}
		catch(Exception e){
			log.error("Problem while connecting to the http endpoint "+e.getStackTrace());
		}
		finally{
			client.getConnectionManager().shutdown();
			httpClient.getConnectionManager().shutdown();
		}
		return buffer.toString();
	}

	public String getTrustStoreFile() {
		return trustStoreFile;
	}

	public void setTrustStoreFile(String trustStoreFile) {
		this.trustStoreFile = trustStoreFile;
	}

	public String getHttpsPort() {
		return httpsPort;
	}

	public void setHttpsPort(String httpsPort) {
		this.httpsPort = httpsPort;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	
	
}