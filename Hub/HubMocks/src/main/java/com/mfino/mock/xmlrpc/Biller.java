package com.mfino.mock.xmlrpc;

import java.util.HashMap;

public class Biller {

	
	public HashMap<String, Object> TopUp(HashMap<String, Object> options) {
	    
		System.out.println("TopUp Request Received with rqid " + options.get("rqid"));
		
	    options.put("status", "00");	    
	    options.put("reffno", "012345");
	    options.put("message", "Biller.TopUp message data");
	    options.put("trace", new Integer(99));
	    
		return options;
	}
	
	public HashMap<String, Object> BillInq(HashMap<String, Object> options) {
		
		System.out.println("Bill Inquiry Request Received with rqid " + options.get("rqid"));
		
		HashMap<String, Object> billdata = new HashMap<String, Object>();
		
		billdata.put("numbillspaid", new Integer(1));
		billdata.put("duedate","        ");
		billdata.put("customername","RIYONO                                  ");		
		billdata.put("numbills", new Integer(1));
		billdata.put("private","062002288114400                 11                000000000000000000000000000000000000                000000000000000000000000000000000000                00000000000000000000000000000000000052520729        000000027580000000000000000000000000RIYONO                                            ");
				
	    options.put("status", "00");
	    options.put("amount", new Integer(800));
	    options.put("reffno", "012345");
	    options.put("message", "Biller.TopUp message data");
	    options.put("trace", new Integer(99));
	    options.put("billdata", billdata);
	    
		return options;
	}

	public HashMap<String, Object> BillPay(HashMap<String, Object> options) {
		
		System.out.println("Bill pay Request Received with rqid " + options.get("rqid"));
		HashMap<String, Object> billdata = new HashMap<String, Object>();
		
		billdata.put("numbillspaid", new Integer(1));
		billdata.put("duedate","        ");
		billdata.put("customername","RIYONO                                  ");		
		billdata.put("numbills", new Integer(1));
		billdata.put("private","062002288114400                 11                000000000000000000000000000000000000                000000000000000000000000000000000000                00000000000000000000000000000000000052520729        000000027580000000000000000000000000RIYONO                                            ");
				
	    
	    options.put("status", "00");
	    options.put("amount", new Integer(800));
	    options.put("reffno", "012345");
	    options.put("message", "Biller.TopUp message data");
	    options.put("trace", new Integer(99));
	    options.put("billdata", billdata);
		return options;
	}
	
public HashMap<String, Object> Reverse(HashMap<String, Object> options) {
		
		System.out.println("Bill pay Reversal Request Received with rqid " + options.get("rqid") + " orgrqid " + options.get("orgrqid") + " orgrqtime "+ options.get("orgrqtime")+" orgmethod "+options.get("orgmethod"));
		/*HashMap<String, Object> billdata = new HashMap<String, Object>();
		
		billdata.put("numbillspaid", new Integer(1));
		billdata.put("duedate","        ");
		billdata.put("customername","RIYONO                                  ");		
		billdata.put("numbills", new Integer(1));
		billdata.put("private","062002288114400                 11                000000000000000000000000000000000000                000000000000000000000000000000000000                00000000000000000000000000000000000052520729        000000027580000000000000000000000000RIYONO                                            ");*/
				
	    
	    options.put("status", "00");
	    options.put("reffno", "012345");
	    options.put("message", "Biller.Reverse message data");
	    options.put("trace", new Integer(99));
	    //options.put("billdata", billdata);
		return options;
	}
	
}
