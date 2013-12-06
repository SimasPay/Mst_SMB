package com.mfino.bsm.iso8583.nm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.crypto.CryptographyService;

public class ISOKeyStore {

	private static Log	       log	= LogFactory.getLog(ISOKeyStore.class);

	private static ISOKeyStore	isoKeyStore;

	private KeyStore	       keyStore;
	private String	           password;
	// private static final String keyStoreType = "JCEKS";

	private String	           MACkey1;
	private String	           MACkey2;
	private String	           MACkey3;

	private void setMACkey1(String mACkey1) {
		MACkey1 = mACkey1;
	}

	private void setMACkey2(String mACkey2) {
		MACkey2 = mACkey2;
	}

	private void setMACkey3(String mACkey3) {
		MACkey3 = mACkey3;
	}

	private File	ksFileInstance;

	private ISOKeyStore(String filePath, String password, String keyStoreType) throws Exception {
		keyStore = KeyStore.getInstance(keyStoreType);
		this.password = password;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {

			if (!filePath.endsWith(File.separator))
				filePath = filePath + File.separator;
			filePath = filePath + "bsmkeystore.jks";

			ksFileInstance = new File(filePath);
			// if (ksFileInstance.exists()) {
			// fis = new FileInputStream(ksFileInstance);
			// keyStore.load(fis, password.toCharArray());
			// }
			// else {
			fos = new FileOutputStream(ksFileInstance);
			keyStore.load(null, password.toCharArray());
			keyStore.store(fos, this.password.toCharArray());
			// }

		}
		finally {
			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		}
	}

	public static ISOKeyStore newInstance(String keyStoreFile, String password, List<String> macKeys) throws Exception {
		if (isoKeyStore == null) {
			isoKeyStore = new ISOKeyStore(keyStoreFile, password, "JCEKS");
			isoKeyStore.setMACkey1(macKeys.get(0));
			isoKeyStore.setMACkey2(macKeys.get(1));
			isoKeyStore.setMACkey3(macKeys.get(2));
			isoKeyStore.storeMasterKey();
		}
		return isoKeyStore;
	}

	public static ISOKeyStore getInstance() throws Exception {
		if (isoKeyStore == null)
			throw new Exception("an instance of isokeystore was not created by calling newInstance");
		return isoKeyStore;
	}

	private void storeMasterKey() throws Exception {
		String masterkey = calculateMasterKey();
		storeKey("MasterKey", masterkey);
	}

	public SecretKey getMasterKey() throws Exception {
		return retrieveKey("MasterKey");
	}

	synchronized public void storeWorkingKey(String workingkey) throws Exception {
		workingkey = workingkey+workingkey.substring(0, 16);
		storeKey("WorkingKey", workingkey);
	}

	public SecretKey getWorkingKey() throws Exception {
		return retrieveKey("WorkingKey");
	}

	private boolean storeKey(String keyName, String hexEncodedKey) throws Exception {

		byte[] bKey = CryptographyService.hexToBin(hexEncodedKey.toCharArray());
		DESedeKeySpec keySpc = new DESedeKeySpec(bKey);
		SecretKeyFactory kf = SecretKeyFactory.getInstance("DESede");
		SecretKey sk = kf.generateSecret(keySpc);

		KeyStore.SecretKeyEntry ske = new KeyStore.SecretKeyEntry(sk);
		keyStore.setEntry(keyName, ske, new KeyStore.PasswordProtection(createSaltedPassword(password)));
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(ksFileInstance);
			keyStore.store(fos, this.password.toCharArray());
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

	private SecretKey retrieveKey(String keyName) {

		SecretKey ske = null;
		try {
			ske = (SecretKey) keyStore.getKey(keyName, createSaltedPassword(this.password));
		}
		catch (Exception ex) {
			log.error("Unable to fetch key", ex);
		}
		return ske;
	}

	public static void main(String[] args) throws Exception {

		List<String> list = new ArrayList<String>();
		list.add("0123456789ABCDEF0123456789ABCDEF");
		list.add("FEDCBA9876543210FEDCBA9876543210");
		list.add("0123456789ABCDEF0123456789ABCDEF");

		ISOKeyStore store = ISOKeyStore.newInstance("C:\\Users\\karthik\\Documents", "mFino260", list);

		String key = store.calculateMasterKey();

		System.out.println(key);

		store.storeMasterKey();

		System.out.println(CryptographyService.binToHex(store.getMasterKey().getEncoded()));
		
	}

	private String calculateMasterKey() throws RuntimeException {
		if (this.MACkey1.length() != 32 || this.MACkey2.length() != 32 || this.MACkey3.length() != 32) {
			throw new RuntimeException("Key(s) is not valid");
		}

		byte[] mac1 = CryptographyService.hexToBin(this.MACkey1.toCharArray());
		byte[] mac2 = CryptographyService.hexToBin(this.MACkey2.toCharArray());
		byte[] mac3 = CryptographyService.hexToBin(this.MACkey3.toCharArray());

		byte[] finalKey = new byte[16];
		for (int i = 0; i < 16; i++)
			finalKey[i] = (byte) (mac1[i] ^ mac2[i]);
		for (int i = 0; i < 16; i++)
			finalKey[i] = (byte) (finalKey[i] ^ mac3[i]);

		String key = new String(CryptographyService.binToHex(finalKey));

		key = key + key.substring(0, 16);

		log.info("final hex master key -->" + key);

		return key;
	}

}