package com.mfino.iso8583.processor.fixtoiso.tobank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMMerchantDompetTransferToBank;
import com.mfino.iso8583.TestUtils;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.iso8583.processor.bank.fixtoiso.MerchantDompetTransfer;
import com.mfino.util.DateTimeUtil;

public class MerchantDompetTransferTest {
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void test1() throws Exception {

		MerchantDompetTransfer bi = new MerchantDompetTransfer();
		CMBankRequest br = TestUtils.newBankRequest();
		CMMerchantDompetTransferToBank msg = new CMMerchantDompetTransferToBank();
		msg.copy(br);
		msg.setAmount(TestUtils.newTransactionAmount());
		msg.setDestCardPAN(TestUtils.newCardPAN());
		msg.setDestMDN(TestUtils.newMDN());
		msg.setUICategory(TestUtils.getStaticUICategory());
		msg.setTransferTime(DateTimeUtil.getLocalTime()	);
		SinarmasISOMessage isoMsg = (SinarmasISOMessage) bi.process(msg);
		assertEquals(msg.getSourceCardPAN(), isoMsg.getPAN());
		Long stan = msg.getTransactionID() % 1000000;
		String paddedSTAN = WrapperISOMessage.padOnLeft(stan.toString(), '0', 6);
		assertTrue(paddedSTAN.contains(isoMsg.getSTAN().toString()));
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertTrue(isoMsg.getTransactionAmount().contains(msg.getAmount().toString()));
		assertTrue(isoMsg.getRRN().contains(msg.getTransactionID().toString()));
		assertEquals(msg.getSourceMDN(), isoMsg.getCardAcceptorIdentificationCode().trim());
		assertEquals(msg.getTransactionID(), (Long) Long.parseLong(isoMsg.getPrivateTransactionID()));
		assertEquals((Integer) Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone), isoMsg.getMerchantType());
		assertEquals(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), isoMsg.getAcquiringInstitutionIdentificationCode());
		assertEquals(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), isoMsg.getForwardInstitutionIdentificationCode());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertEquals(msg.getSourceMDN(), isoMsg.getCardAcceptorIdentificationCode());
		assertEquals("SMS SMART", isoMsg.getCardAcceptorNameLocation().trim());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getAccountIdentification1());
		assertEquals(msg.getDestCardPAN(), isoMsg.getAccountIdentification2());
		int[] elements = { 1, 2, 3, 4, 7, 11, 12, 13, 15, 18, 27, 32, 33, 35, 37, 42, 43, 47, 52, 102, 103 };
		assertTrue(TestUtils.validateISOBitmap(elements, new String(isoMsg.writeData())));
	}

	@Test
	public void test2() throws Exception {
		MerchantDompetTransfer bi = new MerchantDompetTransfer();

		CMBankRequest br = TestUtils.newBankRequest();
		CMMerchantDompetTransferToBank msg = new CMMerchantDompetTransferToBank();
		msg.copy(br);
		msg.setAmount(new BigDecimal(TestUtils.getStaticTransactionAmount()));
		msg.setDestCardPAN(TestUtils.getStaticCardPAN());
		msg.setDestMDN(TestUtils.getStaticMDN());
		msg.setUICategory(TestUtils.getStaticUICategory());
		msg.setTransferTime(TestUtils.getStaticTimestamp());
		SinarmasISOMessage isoMsg = (SinarmasISOMessage) bi.process(msg);
		assertEquals(msg.getSourceCardPAN(), isoMsg.getPAN());
		Long stan = msg.getTransactionID() % 1000000;
		String paddedSTAN = WrapperISOMessage.padOnLeft(stan.toString(), '0', 6);
		assertTrue(paddedSTAN.contains(isoMsg.getSTAN().toString()));
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertTrue(isoMsg.getTransactionAmount().contains(msg.getAmount().toString()));
		assertTrue(isoMsg.getRRN().contains(msg.getTransactionID().toString()));
		assertEquals(msg.getSourceMDN(), isoMsg.getCardAcceptorIdentificationCode().trim());
		assertEquals(msg.getTransactionID(), (Long) Long.parseLong(isoMsg.getPrivateTransactionID()));
		assertEquals((Integer) Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone), isoMsg.getMerchantType());
		assertEquals(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), isoMsg.getAcquiringInstitutionIdentificationCode());
		assertEquals(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), isoMsg.getForwardInstitutionIdentificationCode());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertEquals(msg.getSourceMDN(), isoMsg.getCardAcceptorIdentificationCode());
		assertEquals("SMS SMART", isoMsg.getCardAcceptorNameLocation().trim());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getAccountIdentification1());
		assertEquals(msg.getDestCardPAN(), isoMsg.getAccountIdentification2());
		int[] elements = { 1, 2, 3, 4, 7, 11, 12, 13, 15, 18, 27, 32, 33, 35, 37, 42, 43, 47, 52, 102, 103 };
		assertTrue(TestUtils.validateISOBitmap(elements, new String(isoMsg.writeData())));
	}

}
