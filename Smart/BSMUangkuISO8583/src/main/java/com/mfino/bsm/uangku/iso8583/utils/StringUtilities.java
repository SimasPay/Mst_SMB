package com.mfino.bsm.uangku.iso8583.utils;

public class StringUtilities {

	/**
	 * Returns an array of Strings that was formed by splitting str around the first occurence of 
	 * tokenChar.Array size is 2.
	 * 
	 * @param str
	 * @param tokenChar
	 * @return
	 */
	public static String[] tokenizeString(String str,final char tokenChar) {
		String tokenizedStr = "";
		int i=str.indexOf(tokenChar);
		if(i!=-1) {
			tokenizedStr = str.substring(0, i);
			str = str.substring(i+1,str.length());
		}
		return new String[] {tokenizedStr,str};
	}
	
	public static String trimBeginningChars(String str,char c) {
		int start=0;
		while(start<str.length()) {
			if(str.charAt(start)==c)
				start++;
			else
				break;
		}
		return str.substring(start);
	}
	
	public static String trimEndingChars(String str,char c) {
		int start=str.length();
		start = start-1;
		while(start>=0) {
			if(str.charAt(start)==c)
				start--;
			else
				break;
		}
		return str.substring(0,start+1);
	}
	
	public static String leftPadWithCharacter(String str, int totalLength, String padCharacter){
		if((str == null) || ("".equals(str))) return str;
		
		if(str.length() < totalLength){
			int strLen = str.length();
			for(int i = 0;i < (totalLength - strLen);i++ ){
				str = padCharacter + str;
			}
		}
		
		return str;
	}
	
	public static String rightPadWithCharacter(String str, int totalLength, String padCharacter){
		if((str == null) || ("".equals(str))) return str;
		
		if(str.length() < totalLength){
			int strLen = str.length();
			for(int i = 0;i < (totalLength - strLen);i++ ){
				str =  str + padCharacter;
			}
		}
		
		return str;
	}
	
	public static String replaceNthBlock(String str, char c, int n, String newBlock, int blockSize) {
	    int startPos = str.indexOf(c, 0);
	    while (n-- > 1 && startPos != -1)
	        startPos = str.indexOf(c, startPos+1);
	    StringBuilder sb = new StringBuilder(str);
	    sb.replace(startPos, startPos+blockSize, newBlock);
	    return sb.toString();
	}
}
