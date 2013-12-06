package com.mfino.zte.iso8583;

import java.io.Serializable;



//FIXME if we declare fields and assign iso values to them, we don't need to compute values each time one is accessed.
//FIXME add throws exceptions to all get methods
//FIXME check for the lengths when setvalue, add spaces as required
public abstract class WrapperISOMessage implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected WrapperISOMessage() {
	}

	
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
	
	public abstract String getISOVariant();
}