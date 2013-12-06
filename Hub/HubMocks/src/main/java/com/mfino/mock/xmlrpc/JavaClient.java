package com.mfino.mock.xmlrpc;

import java.net.URL;
import java.util.HashMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun14HttpTransportFactory;


public class JavaClient {
	
	static HashMap<String, String> result;
	static Object[] params;
	//static TrustMgr tstmgr = new TrustMgr();
	
	
	public static void main (String [] args) {
		try {

			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL("https://epayment.infolink.co.id:8002/"));

			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);
			
			XmlRpcSun14HttpTransportFactory trnspt = new XmlRpcSun14HttpTransportFactory(client);
			//trnspt.setSSLSocketFactory(tstmgr.getSSLFactory());
			
			client.setTransportFactory(trnspt);
			
			HashMap<String, String> options = new HashMap<String, String>();
			//HashMap result;
		    options.put("rqtime", "20121219T19:06:05");
		    options.put("rqid", "187651");
		    options.put("billid", "6281317901645");
		    options.put("product", "010003");
		    options.put("amount", "100000");
		    options.put("terminal", "6281315377201");
		    options.put("agent", "12345");
		    options.put("caid", "120900");
		    options.put("sign", "BZsGQwUaQgc=");
		    
		     params = new Object[]{options};
		     
			result = (HashMap<String, String>)client.execute("Biller.Topup", params);

			//System.out.println("The sum is: "+ result.get("status"));

		} catch (Exception exception) {
			System.err.println("JavaClient: " + exception);
		}
		
		System.out.println( result ); 
	}
}
