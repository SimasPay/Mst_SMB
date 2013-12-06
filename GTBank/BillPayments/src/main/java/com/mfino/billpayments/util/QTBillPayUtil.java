package com.mfino.billpayments.util;

/**
 * 
 * @author Sasi
 *
 */
public class QTBillPayUtil {
	
	public static String getRequestReference(String prefixString, Long sctlId){
		if(null == sctlId) return null;
		//Added new parameter prefixString, this would be read from mce.properties. 
		//This could be different for each client. Hence externalized
		// Shashank 30-01-2013
		//String prefixString = "1018";
		int currentLength = (prefixString + sctlId).length();
		
		for(int i=currentLength; i < 12; i++){
			prefixString = prefixString + "0";
		}
		
		return prefixString + sctlId;
	}
	
	//to test this.
	/*public static void main(String[] args) {
		System.out.println(getRequestReference(1023L).length());
		System.out.println(getRequestReference(1023L));
	}*/
}
