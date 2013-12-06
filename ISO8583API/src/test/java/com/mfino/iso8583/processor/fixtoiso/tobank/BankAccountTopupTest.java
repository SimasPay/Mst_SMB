package com.mfino.iso8583.processor.fixtoiso.tobank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountTopupToBank;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.iso8583.TestUtils;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.iso8583.processor.bank.fixtoiso.BankAccountTopup;
import com.mfino.util.DateTimeUtil;

public class BankAccountTopupTest {
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void selfTopup1() throws Exception {
		BankAccountTopup bi = new BankAccountTopup();

		CMBankRequest br = TestUtils.newBankRequest();
		CMBankAccountTopupToBank msg = new CMBankAccountTopupToBank();
		msg.copy(br);
		msg.setAmount(new BigDecimal(TestUtils.newTopupAmount()));
		msg.setDestMDN(msg.getSourceMDN());
		msg.setTransferTime(DateTimeUtil.getLocalTime());
		msg.setTransferID(TestUtils.getTransferID());
		SinarmasISOMessage isoMsg = (SinarmasISOMessage) bi.process(msg);
		assertEquals(msg.getSourceCardPAN(), isoMsg.getPAN());
		assertTrue(isoMsg.getTransactionAmount().contains(msg.getAmount().toString()));
		assertEquals((Long) (msg.getTransactionID() % 1000000), isoMsg.getSTAN());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertTrue(isoMsg.getRRN().contains(msg.getTransactionID().toString()));
		assertEquals(msg.getSourceMDN(), isoMsg.getCardAcceptorIdentificationCode().trim());
		assertEquals(msg.getTransactionID(), (Long) Long.parseLong(isoMsg.getPrivateTransactionID()));
		assertEquals((Integer) Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone), isoMsg.getMerchantType());
		assertEquals(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), isoMsg.getAcquiringInstitutionIdentificationCode());
		assertEquals(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), isoMsg.getForwardInstitutionIdentificationCode());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertEquals("SMS SMART", isoMsg.getCardAcceptorNameLocation().trim());
		int[] elements = { 2, 3, 4, 7, 11, 12, 13, 15, 18, 27, 32, 33, 35, 37, 42, 43, 47, 52 };
		assertTrue(TestUtils.validateISOBitmap(elements, new String(isoMsg.writeData())));
	}

	@Test
	public void test2() throws Exception {
		CMBankAccountTopupToBank msg = new CMBankAccountTopupToBank();
		msg.setSourceCardPAN(TestUtils.getStaticCardPAN());
		msg.setTransactionID(TestUtils.getStaticTransactionID());
		msg.setSourceMDN(TestUtils.getStaticMDN());
		msg.setAmount(new BigDecimal(TestUtils.getStaticTopupAmount()));
		msg.setDestMDN(msg.getSourceMDN());
		msg.setTransferTime(DateTimeUtil.getLocalTime());
		msg.setTransferID(TestUtils.getStaticTransferID());
		
		BankAccountTopup bi = new BankAccountTopup();
		SinarmasISOMessage isoMsg = (SinarmasISOMessage) bi.process(msg);
		assertEquals(msg.getSourceCardPAN(), isoMsg.getPAN());
		assertTrue(isoMsg.getTransactionAmount().contains(msg.getAmount().toString()));
		assertEquals((Long) (msg.getTransactionID() % 1000000), isoMsg.getSTAN());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertTrue(isoMsg.getRRN().contains(msg.getTransactionID().toString()));
		assertEquals(msg.getSourceMDN(), isoMsg.getCardAcceptorIdentificationCode().trim());
		assertEquals(msg.getTransactionID(), (Long) Long.parseLong(isoMsg.getPrivateTransactionID()));
		assertEquals((Integer) Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone), isoMsg.getMerchantType());
		assertEquals(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), isoMsg.getAcquiringInstitutionIdentificationCode());
		assertEquals(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), isoMsg.getForwardInstitutionIdentificationCode());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertEquals("SMS SMART", isoMsg.getCardAcceptorNameLocation().trim());
		int[] elements = { 2, 3, 4, 7, 11, 12, 13, 15, 18, 27, 32, 33, 35, 37, 42, 43, 47, 52 };
		assertTrue(TestUtils.validateISOBitmap(elements, new String(isoMsg.writeData())));
	}
}
