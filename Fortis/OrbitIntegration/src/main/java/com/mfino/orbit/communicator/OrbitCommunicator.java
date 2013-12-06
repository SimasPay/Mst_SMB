package com.mfino.orbit.communicator;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.ws.WebServiceException;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.orbit.NoResponseException;
import com.techzultants.orbit.ws.OrbitWebService;

/**
 * @author Maruthi
 * 
 */
public abstract class OrbitCommunicator {
	
	private static Logger log = LoggerFactory.getLogger(OrbitCommunicator.class);

	protected OrbitWebService orbitService;

	protected String key;

	protected DateFormat df = new SimpleDateFormat("dd MMM, yyyy");

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public OrbitWebService getOrbitService() {
		return orbitService;
	}

	public void setOrbitService(OrbitWebService orbitService) {
		this.orbitService = orbitService;
	}

	public abstract MCEMessage process(MCEMessage mceMessage) throws Exception;

	public String handleWSCommunicationException(Exception e) {
		if (e.getCause() instanceof SocketTimeoutException)
			return MCEUtil.SERVICE_TIME_OUT;
		if(e instanceof WebServiceException)
			return	MCEUtil.SERVICE_UNAVAILABLE;
		else
			log.error("OrbitWebService Error: ",e);

		return MCEUtil.SERVICE_UNAVAILABLE;
	}

	public String getHashValue(String debitac, String creditac,
			BigDecimal amount) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(key.getBytes());
		md.update(debitac.getBytes());
		md.update(creditac.getBytes());
		amount = (amount.multiply(new BigDecimal(100))).setScale(0);
		md.update(amount.toString().getBytes());
		byte[] bytes = md.digest();
		char[] encodeHex = Hex.encodeHex(bytes);
		return new String(encodeHex);
	}
}
