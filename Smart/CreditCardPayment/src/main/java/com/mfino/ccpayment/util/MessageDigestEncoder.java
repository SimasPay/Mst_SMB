package com.mfino.ccpayment.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class MessageDigestEncoder {
	public enum Encoding {
		HEX,
		Base64
	}
	
	public static String SHA1(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {		
		return SHA1(text, Encoding.HEX);
	}
	
	public static String SHA1(String text, Encoding encoding) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		
		md.update(text.getBytes("iso-8859-1"), 0, text.length());		
		byte[] hash = md.digest();		
		
		if(Encoding.Base64.equals(encoding))
			return new String(Base64.encodeBase64(hash));
		
		return new String(Hex.encodeHex(hash));				
	}	
}