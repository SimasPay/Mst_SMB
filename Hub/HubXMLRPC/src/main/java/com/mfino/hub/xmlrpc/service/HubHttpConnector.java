package com.mfino.hub.xmlrpc.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun14HttpTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.hub.xmlrpc.CXMLRPCMsg;

/**
 * 
 * @author Vishal
 *
 */
public class HubHttpConnector {

	private static final Logger log = LoggerFactory.getLogger(HubHttpConnector.class);

	private String timeout;
	private Object[] params;
	private Object result;
	private TrustMgr tstmgr = new TrustMgr();

	public Object sendHttpRequest(String url, Object xmlRequest, String method) throws XmlRpcException, MalformedURLException, SocketTimeoutException {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(url));

			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);

			XmlRpcSun14HttpTransportFactory trnspt = new XmlRpcSun14HttpTransportFactory(client);
			trnspt.setSSLSocketFactory(tstmgr.getSSLFactory());

			client.setTransportFactory(trnspt);	

			result = null;
			params = new Object[]{((CXMLRPCMsg)xmlRequest).getXmlMsg()};

			result = client.execute(method, params);

			if (result == null)
			{
				log.info("The result is NULL ");
				throw new SocketTimeoutException();
			}
			else
				log.info("The sum is: "+ ((HashMap<String,Object>) result).get("status"));

		return result;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

}