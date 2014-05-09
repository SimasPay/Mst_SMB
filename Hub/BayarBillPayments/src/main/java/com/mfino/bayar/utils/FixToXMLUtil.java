package com.mfino.bayar.utils;


public class FixToXMLUtil {
	
	public static String padOnLeft(String str,char paddingChar,int finalLength)throws Exception {
		if(finalLength==str.length())
			return str;
		if(finalLength<str.length())
			throw new Exception("String length is already greater than the final length");
		String s = "";
		for(int i=0;i<finalLength-str.length();i++) 
			s = s +String.valueOf(paddingChar);
		str = s+str;
		return str;
	}
	public static String padOnRight(String str,char paddingChar,int finalLength) throws Exception{
		if(finalLength==str.length())
			return str;
		if(finalLength<str.length())
			throw new Exception("String length is already greater than the final length");
		String s = "";
		for(int i=0;i<finalLength-str.length();i++) 
			s = s+String.valueOf(paddingChar);
		str = str+s;
		return str;
	}
	
	public static String getElement48(String destAccountName,String transactionID,String sourceACName,String destinationACBranchName,String destACType ) throws Exception{
		if(destAccountName.length()>30
				||transactionID.length()>16
				||sourceACName.length()>30
				||destinationACBranchName.length()>30
				||destACType.length()>2){
			throw new Exception("String length is already greater than the required length");
		}
		
		StringBuilder s=new StringBuilder();
		s.append(destAccountName);
		for(int i=0;i<(30-destAccountName.length());i++)
			s.append(" ");
		
		s.append(transactionID);
		for(int i=0;i<(16-transactionID.length());i++)
			s.append(" ");
		
		s.append(sourceACName);
		for(int i=0;i<(30-sourceACName.length());i++)
			s.append(" ");
		
		s.append(destinationACBranchName);
		for(int i=0;i<(30-destinationACBranchName.length());i++)
			s.append(" ");
		
		s.append(destACType);
		for(int i=0;i<(2-destACType.length());i++)
			s.append(" ");		
			
		return s.toString();
	}
	
	
	public static void main(String a[]){
		try {
			System.out.print(FixToXMLUtil.getElement48("", String.valueOf(123456), "", "", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
