package com.mfino.iso8583.processor.fixtoiso.tobank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.TestUtils;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.iso8583.processor.bank.fixtoiso.MoneyTransfer;

public class MoneyTrasferTest {
	
	
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void test1() throws Exception {
		MoneyTransfer bi = new MoneyTransfer();

		CMBankRequest br = TestUtils.newBankRequest();
		CMMoneyTransferToBank msg = new CMMoneyTransferToBank();
		msg.copy(br);
		msg.setTransferTime(new Timestamp());
		msg.setUICategory(CmFinoFIX.TransactionUICategory_EMoney_Purchase);
		msg.setAmount(TestUtils.newTransactionAmount());
		msg.setDestCardPAN(TestUtils.newCardPAN());
		SinarmasISOMessage isoMsg = (SinarmasISOMessage) bi.process(msg);
		assertEquals(msg.getSourceCardPAN(), isoMsg.getPAN());
		Long stan = msg.getTransactionID() % 1000000;
		String paddedSTAN = WrapperISOMessage.padOnLeft(stan.toString(), '0', 6);
		assertTrue(paddedSTAN.contains(isoMsg.getSTAN().toString()));
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertTrue(isoMsg.getRRN().contains(msg.getTransactionID().toString()));
		assertEquals(msg.getSourceMDN(), isoMsg.getCardAcceptorIdentificationCode().trim());
		assertEquals(msg.getTransactionID(), (Long) Long.parseLong(isoMsg.getPrivateTransactionID()));
		assertEquals((Integer) Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone), isoMsg.getMerchantType());
		assertEquals(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), isoMsg.getAcquiringInstitutionIdentificationCode());
		assertEquals(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), isoMsg.getForwardInstitutionIdentificationCode());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertEquals("SMS SMART", isoMsg.getCardAcceptorNameLocation().trim());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getAccountIdentification1());
		assertEquals(msg.getDestCardPAN(), isoMsg.getAccountIdentification2());
		int[] elements = {1, 2, 3,4, 7, 11, 12, 13, 15, 18,27, 32, 33, 35, 37, 42, 43, 47,52,102,103 };
		assertTrue(TestUtils.validateISOBitmap(elements, new String(isoMsg.writeData())));
	}

	@Test
	public void test2() throws Exception {
		MoneyTransfer bi = new MoneyTransfer();

		CMBankRequest br = TestUtils.newBankRequest();
		CMMoneyTransferToBank msg = new CMMoneyTransferToBank();
		msg.copy(br);
		msg.setTransferTime(TestUtils.getStaticTimestamp());
		msg.setUICategory(CmFinoFIX.TransactionUICategory_EMoney_Purchase);
		msg.setAmount(new BigDecimal(TestUtils.getStaticTransactionAmount()));
		msg.setDestCardPAN(TestUtils.getStaticCardPAN());
		SinarmasISOMessage isoMsg = (SinarmasISOMessage) bi.process(msg);
		assertEquals(msg.getSourceCardPAN(), isoMsg.getPAN());
		Long stan = msg.getTransactionID() % 1000000;
		String paddedSTAN = WrapperISOMessage.padOnLeft(stan.toString(), '0', 6);
		assertTrue(paddedSTAN.contains(isoMsg.getSTAN().toString()));
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertTrue(isoMsg.getRRN().contains(msg.getTransactionID().toString()));
		assertEquals(msg.getSourceMDN(), isoMsg.getCardAcceptorIdentificationCode().trim());
		assertEquals(msg.getTransactionID(), (Long) Long.parseLong(isoMsg.getPrivateTransactionID()));
		assertEquals((Integer) Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone), isoMsg.getMerchantType());
		assertEquals(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), isoMsg.getAcquiringInstitutionIdentificationCode());
		assertEquals(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), isoMsg.getForwardInstitutionIdentificationCode());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertEquals("SMS SMART", isoMsg.getCardAcceptorNameLocation().trim());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getAccountIdentification1());
		assertEquals(msg.getDestCardPAN(), isoMsg.getAccountIdentification2());
		int[] elements = {1, 2, 3,4, 7, 11, 12, 13, 15, 18,27, 32, 33, 35, 37, 42, 43, 47,52,102,103 };
		assertTrue(TestUtils.validateISOBitmap(elements, new String(isoMsg.writeData())));
	}
}
