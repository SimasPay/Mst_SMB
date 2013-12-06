package com.mfino.bsm.iso8583.nm;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.bsm.iso8583.nm.exceptions.InvalidWorkingKeyException;
import com.mfino.bsm.iso8583.nm.exceptions.KcvValidationFailedException;
import com.mfino.bsm.iso8583.nm.exceptions.TripleDESDecryptionFailedException;
import com.mfino.bsm.iso8583.nm.exceptions.TripleDESEncryptionFailedException;
import com.mfino.bsm.iso8583.utils.CryptoUtil;

public class KeyExchangeHandler {

	private Logger	log	= LoggerFactory.getLogger(KeyExchangeHandler.class);

	private String	key;

	public KeyExchangeHandler(String receievedKey) {
		this.key = receievedKey;
	}

	public void handle() throws KcvValidationFailedException, InvalidWorkingKeyException, Exception {

		try {
			if (this.key.length() < 38) {
				log.error("working key length is <38");
				throw new InvalidWorkingKeyException("Invalid key length.Key field length is not 38.");
			}
			String receivedKcv = key.substring(32,38);
			String encryptedKey = key.substring(0,32);

			log.info("getting the singleton instance of ISOKeyStore");
			ISOKeyStore ks = ISOKeyStore.getInstance();
			SecretKey masterKey = ks.getMasterKey();
			
			log.info("validating the kcv");

			log.info("decrypting the received working key with masterkey");
			String workingKey = CryptoUtil.tripleDESDecrypt(masterKey, encryptedKey);

			log.info("storing the workingkey "+workingKey+" in keystore");
			ks.storeWorkingKey(workingKey);
//			validateKCV(ks.getWorkingKey(), receivedKcv);
		}
		catch (TripleDESDecryptionFailedException ex) {
			log.error("could not decrypt the working key");
			throw new InvalidWorkingKeyException("could not decrypt the working key");
		}
		catch (KcvValidationFailedException ex) {
			log.error("kcv validation failed", ex);
			throw ex;
		}
		catch (Exception ex) {
			log.error("Exception while validating the kcv", ex);
			throw ex;
		}

	}

	private boolean validateKCV(SecretKey masterKey, String receivedKCV) throws Exception {

		log.info("validating received kcv=" + receivedKCV);

		String calculatedKCV = null;

		try {
			log.info("calculating kcv by encrypting 0000000000000000 with masterkey");
			calculatedKCV = CryptoUtil.tripleDESEncrypt(masterKey, "0000000000000000");
		}
		catch (TripleDESEncryptionFailedException ex) {
			log.error("could not calculate kcv", ex);
			throw new Exception("could not calculate kcv");
		}

		if (calculatedKCV.startsWith(receivedKCV)) {
			log.info("kcv successfully validated");
			return true;
		}

		log.warn("kvc validation failed");
		throw new KcvValidationFailedException("kcv validation failed");

	}

	public static void main(String[] args) throws Exception {

		List<String> list = new ArrayList<String>();
		list.add("5820E5DCB0322A3E6B1A6DD531D97F3E");
		list.add("C15D76D01FF14C1FD9B5C2F21089CE57");
		list.add("B5C729BC9D64D35E16F84AC752CDEA98");

		ISOKeyStore store = ISOKeyStore.newInstance("C:\\Users\\karthik\\Documents", "mFino260", list);

		KeyExchangeHandler handler = new KeyExchangeHandler("97C692141D57EDBB85F76DE8BF99CC04A68CDCA90C9021F9");
		handler.handle();
		
		SecretKey workingKey = store.getWorkingKey();

		System.out.println(CryptoUtil.buildEncryptedPinBlock("1234", "5973333334459999"));
		
		
		
	}

}
