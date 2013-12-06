/**
 * 
 */
package com.mfino.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.mfino.domain.common.KeyTokenPair;
import com.mfino.exceptions.CoreException;
import com.mfino.service.TokenService;
import com.mfino.util.Base64UrlSafe;

/**
 * Service to generate and validate Tokens for authentication.
 * 
 * 
 * @author Chaitanya
 *
 */
public class TokenServiceImpl implements TokenService{

	private static final String ALGORITHM = "DESede";//"AES";
	
	private static final String CIPHER_TRANSFORMATION = ALGORITHM+"/ECB/PKCS5Padding";
	
	private static final String CHARSET = "UTF-8"; 
	
	private static final String DEFAULT = "R@nD0m";
	/**
	 * Generates a TokenPair consisting of a Public Token and a Private Token
	 * 
	 * 
	 * @param userName, String representing the user name of the client
	 * 
	 * @return TokenPair, returns null if any of the userName is null.
	 * 
	 * @throws InvalidKeyException 
	 * @throws CoreException
	 */
	public KeyTokenPair generateToken(String userName) throws CoreException, InvalidKeyException{
		KeyTokenPair keyPair = null;
		if(userName==null){
			return keyPair;
		}
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
			SecretKey key = keyGenerator.generateKey();

			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			StringBuffer buffer = new StringBuffer(userName);
			buffer.append(DEFAULT);
			
			byte[] params = buffer.toString().getBytes(CHARSET);
			byte[] token = cipher.doFinal(params);
			byte[] secretKey = key.getEncoded();
			
			secretKey = Base64UrlSafe.encodeBase64(secretKey);
			token = Base64UrlSafe.encodeBase64(token);
			
			/*MessageDigest md = MessageDigest.getInstance("SHA-256");
			secretKey = md.digest(secretKey);
			token = md.digest(token);
			
			secretKey = Base64.encodeBase64(secretKey);
			token = Base64.encodeBase64(token);*/
		
			keyPair = new KeyTokenPair();
			keyPair.setKey(new String(secretKey, CHARSET));
			keyPair.setToken(new String(token, CHARSET));
			
		} catch (NoSuchAlgorithmException nsae) {
			//This is a standard Algorithm provided by JAVA6, should be available.
			throw new CoreException("KeyGeneration algorithm not found.", nsae);
		} catch (NoSuchPaddingException nspe) {
			//Default padding being used so there should not be an exception
			throw new CoreException("Cipher padding not found.", nspe);
		} catch (IllegalBlockSizeException ibse) {
			//Default settings so should not be an exception
			throw new CoreException("Cipher block size is illegal.", ibse);
		} catch (BadPaddingException bpe) {
			// Default settings so should not be an exception
			throw new CoreException("Cipher bad padding.", bpe);
		} catch (InvalidKeyException ike) {
			//This should be thrown to the caller
			throw ike;
		} catch (UnsupportedEncodingException usee) {
			// UTF-8 is a standard encoding should not be an exception
			throw new CoreException("Failed to encode in UTF-8.", usee);
		}
		
		return keyPair;
	}
	
	/**
	 * Validates the Token pair, if the token and key match then returns <code>true</code>, 
	 * otherwise returns <code>false</code>.
	 * 
	 * @param keyTokenPair - TokenPair
	 * @param userName - String
	 * 
	 * @return boolean
	 * @throws CoreException 
	 * @throws InvalidKeyException 
	 */
	public boolean validateToken(KeyTokenPair keyTokenPair, String userName) throws CoreException, InvalidKeyException{
		boolean isTokenKeyPairValid = false;
		if(keyTokenPair==null || keyTokenPair.getKey()==null || keyTokenPair.getToken()==null
				|| userName==null){
			return isTokenKeyPairValid;
		}
		 
		try {
			byte[] keyBytes = keyTokenPair.getKey().getBytes(CHARSET);
			byte[] token = keyTokenPair.getToken().getBytes(CHARSET);
			
			keyBytes = Base64UrlSafe.decodeBase64(keyBytes);
			token = Base64UrlSafe.decodeBase64(token);
			
			DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
			SecretKey key = keyFactory.generateSecret(keySpec);

			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, key);
			
			byte[] clearText = cipher.doFinal(token);
			
			StringBuffer buffer = new StringBuffer(userName);
			buffer.append(DEFAULT);
			
			byte[] params = buffer.toString().getBytes(CHARSET);
			String input = new String(params, CHARSET);
			String decryptString = new String(clearText, CHARSET);
			
			isTokenKeyPairValid = input.equals(decryptString);
		} catch (NoSuchAlgorithmException nsae) {
			//This is a standard Algorithm provided by JAVA6, should be available.
			throw new CoreException("KeyGeneration algorithm not found.", nsae);
		} catch (InvalidKeySpecException e) {
			//This is a standard KeySpec provided by JAVA6, should be available.
			
		} catch (NoSuchPaddingException nspe) {
			//Default settings so should not be an exception
			throw new CoreException("Cipher padding not found.", nspe);
		} catch (InvalidKeyException ike) {
			//This should be thrown to the caller
			throw ike;
		} catch (IllegalBlockSizeException ibse) {
			//Default settings so should not be an exception
			throw new CoreException("Cipher block size is illegal.", ibse);
		} catch (BadPaddingException bpe) {
			//Default settings so should not be an exception
			throw new CoreException("Cipher bad padding.", bpe);
		} catch (UnsupportedEncodingException usee) {
			//UTF-8 is a standard encoding should not be an exception
			throw new CoreException("Failed to encode in UTF-8.", usee);
		}
		
		
		return isTokenKeyPairValid;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TokenServiceImpl service = new TokenServiceImpl();
		
		String userName = "ucganesh";
		String password = "mf!n0pwd45%@23423434";
		
		KeyTokenPair keyTokenPair;
		try {
			keyTokenPair = service.generateToken(userName);
			System.out.println("Key: "+keyTokenPair.getKey());
			System.out.println("Token: "+keyTokenPair.getToken());
			
			boolean isValid = service.validateToken(keyTokenPair, userName);
			
			System.out.println("Decryption Success: "+isValid);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}
