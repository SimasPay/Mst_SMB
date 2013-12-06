package com.mfino.iso8583.processor.fixtoiso.tobank.billpayments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMBillPaymentBankRequest;
import com.mfino.iso8583.TestUtils;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.iso8583.processor.bank.billpayments.fixtoiso.BillPayment;
import com.mfino.iso8583.processor.bank.fixtoiso.BalanceInquiry;

public class BillPaymentTest {
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void test1() throws Exception {
		BillPayment bi = new BillPayment();

		CMBillPaymentBankRequest br = TestUtils.newBillPaymentBankRequest();
		CMBalanceInquiryToBank msg = new CMBalanceInquiryToBank();
		msg.copy(br);

		SinarmasISOMessage isoMsg = (SinarmasISOMessage) bi.process(msg);
		assertEquals(msg.getSourceCardPAN(), isoMsg.getPAN());
		assertEquals("000000000000", isoMsg.getTransactionAmount());
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
		int[] elements = {2,3,4,7,11,12,13,15,18,27,32,33,35,37,42,43,47,52};
		assertTrue(TestUtils.validateISOBitmap(elements, new String(isoMsg.writeData())));
	}

	@Test
	public void test2() throws Exception {
		CMBalanceInquiryToBank msg = new CMBalanceInquiryToBank();
		msg.setSourceCardPAN(TestUtils.getStaticCardPAN());
		msg.setTransactionID(TestUtils.getStaticTransactionID());
		msg.setSourceMDN(TestUtils.getStaticMDN());

		BalanceInquiry bi = new BalanceInquiry();
		SinarmasISOMessage isoMsg = (SinarmasISOMessage) bi.process(msg);
		assertEquals(msg.getSourceCardPAN(), isoMsg.getPAN());
		assertEquals("000000000000", isoMsg.getTransactionAmount());
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
		int[] elements = {2,3,4,7,11,12,13,15,18,27,32,33,35,37,42,43,47,52};
		assertTrue(TestUtils.validateISOBitmap(elements, new String(isoMsg.writeData())));
	}

}
