package com.mfino.fidelity.iso8583.utils;


public class FixToISOUtil {
	
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
	public static String getPaddedSTAN(String stan) {
		if(stan.length()<12){
			while(stan.length()!=12)
				stan = "0"+stan;
			return stan;
		}else{ 
			return stan.substring(stan.length()-12);
		}			
	}
	
	public static void main(String args[]){
		System.out.println(getPaddedSTAN("012345678912"));
	}
	
}
