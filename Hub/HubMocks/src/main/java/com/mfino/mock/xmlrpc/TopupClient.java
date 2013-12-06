package com.mfino.mock.xmlrpc;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun14HttpTransportFactory;

import com.mfino.hibernate.Timestamp;


public class TopupClient {
	
	static Object result;
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
			
			HashMap<String, Object> options = new HashMap<String, Object>();
			//HashMap result;
			
			TimeZone tz = TimeZone.getTimeZone("GMT");
		    Date date = Calendar.getInstance().getTime();

		    SimpleDateFormat date_format_gmt_all = new SimpleDateFormat("yyyyMMddHHmmss");// Standard format MM/dd/yyyy HH:mm:ss
		    DateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmss");
		    date_format_gmt_all.setTimeZone(tz);
		    Date date_gmt = date_format.parse(date_format_gmt_all.format(date));
		    
		    String rqid = "287672", billid = "08322387510", product = "013101";
		    Integer amount = new Integer(10000);
		    options.put("rqtime", new Timestamp());
		    options.put("rqid", rqid);
		    options.put("billid", billid);
		    options.put("product", product);
		    options.put("amount", amount);
		    options.put("terminal", "6281315377201");
		    options.put("agent", "12345");
		    options.put("caid", "121000");
		    //String sign = GenerateSignature.getRequestSign("121000", "123456", Long.parseLong(rqid), new Timestamp(), billid, new BigDecimal(amount));
		    System.out.println("Signature : " + amount);
		    options.put("sign", "");
		    
			params = new Object[]{options};
		    
			result = client.execute("Biller.TopUp", params);

			if (result == null)
				System.out.println("The result is NULL ");
			else
				System.out.println("The sum is: "+ ((HashMap<String,Object>) result).get("status"));

		} catch (Exception exception) {
			System.err.println("JavaClient: " + exception);
		}
		
		//System.out.println( result ); 
	}
}
