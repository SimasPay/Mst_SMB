package com.mfino.bayar.service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustMgr {

	// Install the all-trusting trust manager
	public SSLContext sc = null;

	public SSLSocketFactory getSSLFactory() {


		try {
			sc = SSLContext.getInstance("SSL");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Create empty HostnameVerifier
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					public void checkClientTrusted(X509Certificate[] certs, String authType) {
						// Trust always
					}

					public void checkServerTrusted(X509Certificate[] certs, String authType) {
						// Trust always
					}
				}
		};

		try {
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		
		return sc.getSocketFactory();
	}
}
