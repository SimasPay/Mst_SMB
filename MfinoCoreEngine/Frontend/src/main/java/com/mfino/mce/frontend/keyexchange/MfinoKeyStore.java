package com.mfino.mce.frontend.keyexchange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.crypto.CryptographyService;

public class MfinoKeyStore {

	private static Log	log	= LogFactory.getLog(MfinoKeyStore.class);
	
	private static MfinoKeyStore	fks;

	private KeyStore	            keyStore;
	private String	                keyStorepassword;
	private File	                keyStoreFile;
	private static final String	    keyStoreType	= "JCEKS";

	private MfinoKeyStore(String keyStoreFilepath, String password) throws Exception {
		keyStore = KeyStore.getInstance(keyStoreType);
		this.keyStorepassword = password;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			keyStoreFile = new File(keyStoreFilepath);
			if (keyStoreFile.exists()) {
				fis = new FileInputStream(keyStoreFile);
				keyStore.load(fis, password.toCharArray());
			}
			else {
				fos = new FileOutputStream(keyStoreFile);
				keyStore.load(null, password.toCharArray());
				keyStore.store(fos, this.keyStorepassword.toCharArray());
			}
		}
		finally {
			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		}
	}

	public static MfinoKeyStore getFrontEndKeyStore(String keyStoreFilepath, String password) {
		try {
			if (fks == null)
				fks = new MfinoKeyStore(keyStoreFilepath, password);
		}
		catch (Exception ex) {
			log.error("Error creating key store", ex);
			fks = null;
		}
		return fks;
	}

	/**
	 * public static FrontendKeyStore getNewFrontEndKeyStore(String
	 * keyStoreFilepath, String password) throws Exception { if (fks == null) {
	 * fks = new FrontendKeyStore(keyStoreFilepath, password, true); } return
	 * fks; }
	 */
	/**
	 * DES-EDE key
	 * 
	 * @param key1
	 * @throws Exception
	 */
	public void putMACKey1(String key, String password) throws Exception {
		putMainKeyInKeyStore("MACKey1", key, password);
	}

	/**
	 * DES-EDE key
	 * 
	 * @return
	 * @throws Exception
	 */
	public SecretKey getMACKey1(String password) throws Exception {
		return getKey("MACKey1", password);
	}

	/**
	 * DES-EDE key
	 * 
	 * @param key2
	 * @throws Exception
	 */
	public void putMACKey2(String key, String password) throws Exception {
		putMainKeyInKeyStore("MACKey2", key, password);
	}

	/**
	 * DES-EDE key
	 * 
	 * @return
	 * @throws Exception
	 */
	public SecretKey getMACKey2(String password) throws Exception {
		return getKey("MACKey2", password);
	}

	/**
	 * DES-EDE key
	 * 
	 * @param pinKey
	 * @throws Exception
	 */
	public void putPinKey(String key, String password) throws Exception {
		putMainKeyInKeyStore("PinKey", key, password);
	}

	/**
	 * DES-EDE key
	 * 
	 * @return
	 * @throws Exception
	 */
	public SecretKey getPinKey(String password) throws Exception {
		return getKey("PinKey", password);
	}
	
	/**
	 * DES-EDE key
	 * 
	 * @param MasterKey
	 * @throws Exception
	 */
	public void putMasterKey(String masterkey, String password) throws Exception {
		putMainKeyInKeyStore("MasterKey", masterkey, password);
	}

	/**
	 * DES-EDE key
	 * 
	 * @return
	 * @throws Exception
	 */
	public SecretKey getMasterKey(String password) throws Exception {
		return getKey("MasterKey", password);
	}
	
	
	private boolean putMainKeyInKeyStore(String keyName, String hexEncodedKey, String password) throws Exception {

		byte[] bKey = CryptographyService.hexToBin(hexEncodedKey.toCharArray());
		DESedeKeySpec keySpc = new DESedeKeySpec(bKey);
		SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");
		SecretKey sk = kf.generateSecret(keySpc);

		KeyStore.SecretKeyEntry ske = new KeyStore.SecretKeyEntry(sk);
		keyStore.setEntry(keyName, ske, new KeyStore.PasswordProtection(createSaltedPassword(password)));
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(keyStoreFile);
			keyStore.store(fos, keyStorepassword.toCharArray());
		}
		finally {
			if (fos != null)
				fos.close();
		}
		return true;
	}

	private char[] createSaltedPassword(String password) throws Exception {

		Mac mac = Mac.getInstance("HmacSHA1");
		SecretKeySpec secret = new SecretKeySpec(password.getBytes("UTF-16"), "HmacSHA1");
		mac.init(secret);
		byte[] digest = mac.doFinal(password.getBytes());
		return CryptographyService.binToHex(digest);
	}

	private SecretKey getKey(String mainKey, String password) {
		
		SecretKey ske=null;
        try {
	        ske = (SecretKey) keyStore.getKey(mainKey, createSaltedPassword(password));
        }
        catch (UnrecoverableKeyException ex) {
        	log.error("Unable to fetch key", ex);
        }
        catch (KeyStoreException ex) {
        	log.error("Unable to fetch key", ex);
        }
        catch (NoSuchAlgorithmException ex) {
        	log.error("Unable to fetch key", ex);
        }
        catch (Exception ex) {
        	log.error("Unable to fetch key", ex);
        }
		return ske;
	}

	public static void main(String[] args) throws Exception {
		MfinoKeyStore fks = MfinoKeyStore.getFrontEndKeyStore("C:\\Users\\karthik\\Desktop\\keystore.jks", "123456");
		String keyStr = "012345678901234567890123456789012345678901234567";
		fks.putMACKey1( keyStr,"123456");
		SecretKey sk = fks.getMACKey1("123456");
		byte[] bs = sk.getEncoded();
		for (int i = 0; i < bs.length; i++)
			System.out.print(bs[i]);
		System.out.println(bs.toString());
		System.out.println(new String(CryptographyService.binToHex(bs)).equals(keyStr));
	}
}
