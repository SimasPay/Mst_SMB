/**
 * 
 */
package com.mfino.sms;

import java.security.MessageDigest;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCheckBalance;
import com.mfino.sms.handlers.CheckBalanceHandler;
import com.mfino.util.HibernateUtil;

/**
 * @author Deva
 * 
 */
public class CheckBalanceHandlerTest extends TestCase {

	@Override
	public void setUp() {
		HibernateUtil.getCurrentSession().beginTransaction();
	}

	@Override
	public void tearDown() {
		HibernateUtil.getCurrentSession().getTransaction().rollback();
	}

	@Test
	public void testCheckBalanceSuccess() {
		CMCheckBalance checkBalanceRequest = new CMCheckBalance();
		checkBalanceRequest.setServiceName(CmFinoFIX.ServiceName_CHECK_BALANCE);
		checkBalanceRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
		checkBalanceRequest.setSourceMDN("628811586624");
		checkBalanceRequest.setPin("123456");
		CheckBalanceHandler checkBalanceHandler = new CheckBalanceHandler(
				checkBalanceRequest, "SAL.123456");
		checkBalanceHandler.handle();
		assertNotNull(checkBalanceHandler.getMessageToSend());
	}

	@Test
	public void testCheckBalanceInvalidPIN() {
		CMCheckBalance checkBalanceRequest = new CMCheckBalance();
		checkBalanceRequest.setServiceName(CmFinoFIX.ServiceName_CHECK_BALANCE);
		checkBalanceRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
		checkBalanceRequest.setSourceMDN("628811214746");
		checkBalanceRequest.setPin("123456");
		CheckBalanceHandler checkBalanceHandler = new CheckBalanceHandler(
				checkBalanceRequest, "SAL.123456");
		checkBalanceHandler.handle();
		assertNotNull(checkBalanceHandler.getMessageToSend());
	}

	@Test
	public void testCheckBalanceInvalidMDN() {
		CMCheckBalance checkBalanceRequest = new CMCheckBalance();
		checkBalanceRequest.setServiceName(CmFinoFIX.ServiceName_CHECK_BALANCE);
		checkBalanceRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
		checkBalanceRequest.setSourceMDN("6212345678");
		checkBalanceRequest.setPin("123456");
		CheckBalanceHandler checkBalanceHandler = new CheckBalanceHandler(
				checkBalanceRequest, "SAL.123456");
		checkBalanceHandler.handle();
		assertNotNull(checkBalanceHandler.getMessageToSend());
	}

	@Test
	public void testCheckBalanceNotActive() {
		CMCheckBalance checkBalanceRequest = new CMCheckBalance();
		checkBalanceRequest.setServiceName(CmFinoFIX.ServiceName_CHECK_BALANCE);
		checkBalanceRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
		checkBalanceRequest.setSourceMDN("628811367931");
		checkBalanceRequest.setPin("123456");
		CheckBalanceHandler checkBalanceHandler = new CheckBalanceHandler(
				checkBalanceRequest, "SAL.123456");
		checkBalanceHandler.handle();
		assertNotNull(checkBalanceHandler.getMessageToSend());
	}

	@Test
	public void testCheckBalanceTempLocked() {
		CMCheckBalance checkBalanceRequest = new CMCheckBalance();
		checkBalanceRequest.setServiceName(CmFinoFIX.ServiceName_CHECK_BALANCE);
		checkBalanceRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
		checkBalanceRequest.setSourceMDN("628811586732");
		checkBalanceRequest.setPin("123456");
		CheckBalanceHandler checkBalanceHandler = new CheckBalanceHandler(
				checkBalanceRequest, "SAL.123456");
		checkBalanceHandler.handle();
		assertNotNull(checkBalanceHandler.getMessageToSend());
	}

	@Test
	public void testCheckBalancePINReset() {
		CMCheckBalance checkBalanceRequest = new CMCheckBalance();
		checkBalanceRequest.setServiceName(CmFinoFIX.ServiceName_CHECK_BALANCE);
		checkBalanceRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
		checkBalanceRequest.setSourceMDN("628811127281");
		checkBalanceRequest.setPin("123456");
		CheckBalanceHandler checkBalanceHandler = new CheckBalanceHandler(
				checkBalanceRequest, "SAL.123456");
		checkBalanceHandler.handle();
		assertNotNull(checkBalanceHandler.getMessageToSend());
	}

	@Test
	public void testCheckBalancePocketNotActive() {
		CMCheckBalance checkBalanceRequest = new CMCheckBalance();
		checkBalanceRequest.setServiceName(CmFinoFIX.ServiceName_CHECK_BALANCE);
		checkBalanceRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
		checkBalanceRequest.setSourceMDN("628811621098");
		checkBalanceRequest.setPin("123456");
		CheckBalanceHandler checkBalanceHandler = new CheckBalanceHandler(
				checkBalanceRequest, "SAL.123456");
		checkBalanceHandler.handle();
		assertNotNull(checkBalanceHandler.getMessageToSend());
	}

	public static void updatedPin(String[] args) {
		if (args == null || args.length < 2) {
			System.out.println("Invalid Method call");
			return;
		}
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(args[0].getBytes());
			md.update(args[1].getBytes()); //
			byte[] bytes = md.digest();
			char[] encodeHex = Hex.encodeHex(bytes);
			String calcPIN = new String(encodeHex);
			calcPIN = calcPIN.toUpperCase();
			System.out.println(calcPIN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
