/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.constants;

/**
 * 
 * @author Siddhartha Chinthapally
 */
public class SecurityConstants {
	public static final int	    MAX_LOGIN_TRIES	   = 5;
	public static final String DESEDE_CBC_PKCS5 = "DESede/CBC/PKCS5Padding";
	public static final String DESEDE_CBC_PKCS7 = "DESede/CBC/PKCS7Padding";
	public static final String DESEDE_CBC_NOPADDING = "DESede/CBC/NoPadding";
	public static final String DESEDE_ECB_NOPADDING = "DESede/ECB/NoPadding";
	public static final String DESEDE_ECB_PKCS7 = "DESede/ECB/PKCS7Padding";
	public static final String DESEDE_ECB_PKCS5 = "DESede/ECB/PKCS5Padding";
	public static final String DESEDE_NOMODE= "DESede/NONE";
	public static final String DESEDE = "DESede";
	public static final String DES = "DES";
	public static final String BOUNCYCASTLE_PROVIDER = "BC";
	public static final String AES = "AES";
	public static final String SHA1PRNG = "SHA1PRNG";
	public static final String DES_CBC_NOPADDING ="DES/CBC/NoPadding";
	public static final int KEY_REUSE_LIMIT=18;
	public static final int TIME_SINCE_LAST_LOGIN_MILLIS=600000;
}
