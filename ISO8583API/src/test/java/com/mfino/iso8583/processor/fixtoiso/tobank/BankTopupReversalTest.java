package com.mfino.iso8583.processor.fixtoiso.tobank;

import static com.mfino.fix.CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountTopupReversalToBank;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.TestUtils;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.iso8583.processor.bank.fixtoiso.BankTopupReversal;
import com.mfino.util.DateTimeUtil;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class BankTopupReversalTest {
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void test1() throws Exception {
		BankTopupReversal bi = new BankTopupReversal();

		CMBankRequest br = TestUtils.newBankRequest();
		CMBankAccountTopupReversalToBank msg = new CMBankAccountTopupReversalToBank();
		msg.copy(br);
		msg.setAmount(new BigDecimal(TestUtils.newTopupAmount()));
		msg.setDestMDN(msg.getSourceMDN());
		msg.setTransferTime(DateTimeUtil.getLocalTime());
		msg.setTransferID(TestUtils.getTransferID());
		msg.setBankSystemTraceAuditNumber(TestUtils.newBankSTAN());
		msg.setBankRetrievalReferenceNumber(TestUtils.newBankRRN());
		SinarmasISOMessage isoMsg = (SinarmasISOMessage) bi.process(msg);
		assertEquals(msg.getSourceCardPAN(), isoMsg.getPAN());
		assertTrue(isoMsg.getTransactionAmount().contains(msg.getAmount().toString()));
		Long stan = Long.parseLong(msg.getBankSystemTraceAuditNumber());
		stan = stan % 1000000;
		String paddedSTAN = WrapperISOMessage.padOnLeft(stan.toString(), '0', 6);
		assertTrue(paddedSTAN.contains(isoMsg.getSTAN().toString()));
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertTrue(isoMsg.getRRN().contains(msg.getBankRetrievalReferenceNumber()));
		assertEquals(msg.getSourceMDN(), isoMsg.getCardAcceptorIdentificationCode().trim());
		assertEquals(msg.getTransactionID(), (Long) Long.parseLong(isoMsg.getPrivateTransactionID()));
		assertEquals((Integer) Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone), isoMsg.getMerchantType());
		assertEquals(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), isoMsg.getAcquiringInstitutionIdentificationCode());
		assertEquals(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), isoMsg.getForwardInstitutionIdentificationCode());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertEquals("SMS SMART", isoMsg.getCardAcceptorNameLocation().trim());
		String reversalInfoStr = "0200" + paddedSTAN;
		assertEquals(reversalInfoStr, isoMsg.getSinarmasReversalInfo().substring(0, 10));
		IsoValue<Timestamp> isoValue = new IsoValue<Timestamp>(IsoType.DATE10, new Timestamp());
		reversalInfoStr = reversalInfoStr + isoValue.toString();
		reversalInfoStr = reversalInfoStr + ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString();
		reversalInfoStr = reversalInfoStr + ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString();;
		assertEquals(reversalInfoStr.substring(20), isoMsg.getSinarmasReversalInfo().substring(20));
		int[] elements = { 1, 2, 3, 4, 7, 11, 12, 13, 15, 18, 27, 32, 33, 35, 37, 42, 43, 47, 90 };
		assertTrue(TestUtils.validateISOBitmap(elements, new String(isoMsg.writeData())));
	}

	@Test
	public void test2() throws Exception {
		BankTopupReversal bi = new BankTopupReversal();

		CMBankRequest br = TestUtils.newBankRequest();
		CMBankAccountTopupReversalToBank msg = new CMBankAccountTopupReversalToBank();
		msg.copy(br);
		msg.setAmount(new BigDecimal(TestUtils.getStaticTopupAmount()));
		msg.setDestMDN(msg.getSourceMDN());
		msg.setTransferTime(DateTimeUtil.getLocalTime());
		msg.setTransferID(TestUtils.getStaticTransferID());
		msg.setBankSystemTraceAuditNumber(TestUtils.getStaticBankSTAN());
		msg.setBankRetrievalReferenceNumber(TestUtils.getStaticBankRRN());
		SinarmasISOMessage isoMsg = (SinarmasISOMessage) bi.process(msg);
		assertEquals(msg.getSourceCardPAN(), isoMsg.getPAN());
		assertTrue(isoMsg.getTransactionAmount().contains(msg.getAmount().toString()));
		Long stan = Long.parseLong(msg.getBankSystemTraceAuditNumber());
		stan = stan % 1000000;
		String paddedSTAN = WrapperISOMessage.padOnLeft(stan.toString(), '0', 6);
		assertTrue(paddedSTAN.contains(isoMsg.getSTAN().toString()));
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertTrue(isoMsg.getRRN().contains(msg.getBankRetrievalReferenceNumber()));
		assertEquals(msg.getSourceMDN(), isoMsg.getCardAcceptorIdentificationCode().trim());
		assertEquals(msg.getTransactionID(), (Long) Long.parseLong(isoMsg.getPrivateTransactionID()));
		assertEquals((Integer) Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone), isoMsg.getMerchantType());
		assertEquals(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), isoMsg.getAcquiringInstitutionIdentificationCode());
		assertEquals(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), isoMsg.getForwardInstitutionIdentificationCode());
		assertEquals(msg.getSourceCardPAN(), isoMsg.getTrack2Data());
		assertEquals("SMS SMART", isoMsg.getCardAcceptorNameLocation().trim());
		String reversalInfoStr = "0200" + paddedSTAN;
		assertEquals(reversalInfoStr, isoMsg.getSinarmasReversalInfo().substring(0, 10));
		IsoValue<Timestamp> isoValue = new IsoValue<Timestamp>(IsoType.DATE10, new Timestamp());
		reversalInfoStr = reversalInfoStr + isoValue.toString();
		reversalInfoStr = reversalInfoStr + ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString();
		reversalInfoStr = reversalInfoStr + ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString();;
		assertEquals(reversalInfoStr.substring(20), isoMsg.getSinarmasReversalInfo().substring(20));
		int[] elements = { 1, 2, 3, 4, 7, 11, 12, 13, 15, 18, 27, 32, 33, 35, 37, 42, 43, 47, 90 };
		assertTrue(TestUtils.validateISOBitmap(elements, new String(isoMsg.writeData())));
	}
}
