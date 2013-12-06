package com.mfino.iso8583;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.iso8583.processor.bank.billpayments.UMGH2HISOMessage;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

public class UMGISOMessageTest {
	private MessageFactory	MsgFactory;

	private void parseConfigFile() throws IOException {
		MsgFactory = ConfigParser.createFromClasspathConfig("config.xml");
	}

	@Before
	public void setUp() throws Exception {
		if (MsgFactory == null)
			parseConfigFile();
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void requestParametersTest() throws Exception {
		// String
		// str="0200723A4021A8601000166396873100109882300000000000000000042317010816914400010904240424601760388103881166396873100109882191679736190628811000656 SMART SMS 012191679736190B08232216A6143AA";
		try {
			UMGH2HISOMessage original = new UMGH2HISOMessage(new IsoMessage());
			original.setType(0x200);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			Timestamp presentTime = new Timestamp(cal.getTime());
			cal.clear();
			cal.set(0, 3, 24, 17, 1, 8);
			Timestamp ts = new Timestamp(cal.getTime());
			original.setBinary(false);
			original.setPAN("6396873100109882");// 2
			original.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Balance_Inquiry));// 3
			original.setTransactionAmount("14123");// 4
			original.setTransmissionTime(ts);// 7
			original.setSTAN(169144L);// 11
			original.setLocalTransactionTime(ts);// 12
			original.setLocalTransactionDate(ts);// 13
			original.setExpirationDate(ts);//14
			original.setSettlementDate(ts);// 15
			original.setMerchantType(Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone));// 18
			original.setPointOfServiceEntryMode(123);//22
			original.setNetworkIdentificationId(196);//24
			original.setAuthorizingIdentificationResponseLength(6);// 27
			original.setAcquiringInstitutionIdentificationCode(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString());// 32
			original.setForwardInstitutionIdentificationCode(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString());// 33
			original.setTrack2Data("6396873100109882");// 35
			original.setRRN("191679736190");// 37
			//			original.setAuthorizationIdentificationResponse("209741");//38
			//			original.setResponseCode("00");//39
			original.setCardAcceptorTerminalIdentification("15388101");//41
			original.setCardAcceptorIdentificationCode("628812400656");// 42
			original.setCardAcceptorNameLocation("SMS SMART");// 43
			//FIXME have to confirm about 48
			original.setEncryptedPin("B08232216A6143AA");// 52
			//			original.setNetworkManagementInformationCode(101);//70
			original.setAccountIdentification1("6396873100145472");//102
			original.setAccountIdentification2("6396873100145373");//103

			byte[] atBank = original.writeData();
			IsoMessage iso = MsgFactory.parseMessage(atBank, 0);
			SinarmasISOMessage received = new SinarmasISOMessage(iso);
			String str = new String(atBank);

			//			assertEquals(
			//			        str,
			//			        "0200723A4021A8621000166396873100109882300000000000000000042422310816914422310804240424601760388103881166396873100109882191679736190628812400656   SMS SMART                               012191679736190B08232216A6143AA");

			assertEquals(received.getPAN(), original.getPAN());
			assertEquals(received.getProcessingCode(), original.getProcessingCode());
			assertEquals(received.getTransactionAmount(), original.getTransactionAmount());

			Timestamp ts1 = received.getTransmissionTime();
			Timestamp ts2 = original.getTransmissionTime();
			//			assertEquals(received.getTransmissionTime(), original.getTransmissionTime());
			assertEquals(received.getSTAN(), original.getSTAN());
			//			assertEquals(received.getLocalTransactionTime(), original.getLocalTransactionTime());
			//			assertEquals(received.getLocalTransactionDate(), original.getLocalTransactionDate());
			//			assertEquals(received.getSettlementDate(), original.getSettlementDate());
			assertEquals(received.getMerchantType(), original.getMerchantType());
			assertEquals(received.getFunctionCode(), original.getFunctionCode());
			assertEquals(received.getAuthorizingIdentificationResponseLength(), original.getAuthorizingIdentificationResponseLength());
			assertEquals(received.getAcquiringInstitutionIdentificationCode(), original.getAcquiringInstitutionIdentificationCode());
			assertEquals(received.getForwardInstitutionIdentificationCode(), original.getForwardInstitutionIdentificationCode());
			assertEquals(received.getTrack2Data(), original.getTrack2Data());
			assertEquals(received.getRRN(), received.getRRN());
			assertEquals(received.getCardAcceptorTerminalIdentification(), original.getCardAcceptorTerminalIdentification());
			assertEquals(received.getCardAcceptorIdentificationCode(), original.getCardAcceptorIdentificationCode());
			assertEquals(received.getCardAcceptorNameLocation(), original.getCardAcceptorNameLocation());
			assertEquals(received.getEncryptedPin(), original.getEncryptedPin());
			assertEquals(received.getAccountIdentification1(), original.getAccountIdentification1());
			assertEquals(received.getAccountIdentification2(), original.getAccountIdentification2());
		}
		catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		catch (ParseException ex) {
			ex.printStackTrace();
		}

	}

	@Test
	public void responseParametersTest() throws Exception {
		try {
			SinarmasISOMessage original = new SinarmasISOMessage(new IsoMessage());
			original.setType(0x210);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			cal.clear();
			cal.set(0, 3, 24, 17, 1, 8);
			Timestamp ts = new Timestamp(cal.getTime());
			original.setBinary(false);
			original.setPAN("6396873100109882");// 2
			original.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Balance_Inquiry));// 3
			original.setTransactionAmount("000000000000");// 4
			original.setTransmissionTime(ts);// 7
			original.setSTAN(169144L);// 11
			original.setLocalTransactionTime(ts);// 12
			original.setLocalTransactionDate(ts);// 13
			original.setSettlementDate(ts);// 15
			original.setMerchantType(Integer.parseInt(CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone));// 18
			original.setFunctionCode(196);//24
			original.setAuthorizingIdentificationResponseLength(6);// 27
			original.setAcquiringInstitutionIdentificationCode(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString());// 32
			original.setForwardInstitutionIdentificationCode(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString());// 33
			original.setTrack2Data("6396873100109882");// 35
			original.setRRN("191679736190");// 37
			original.setAuthorizationIdentificationResponse("209741");//38
			original.setResponseCode("00");//39
			original.setCardAcceptorTerminalIdentification("15388101");//41
			original.setCardAcceptorIdentificationCode("628812400656");// 42
			original.setCardAcceptorNameLocation("SMS SMART");// 43
			original.setPrivateTransactionID("191679736190");// 47
			//FIXME have to confirm about 48
			original.setEncryptedPin("B08232216A6143AA");// 52
			original.setAdditionalAmounts("1002360C000000003426");//54
			original.setLastTransactions("010309DATMWit");
			//			original.setNetworkManagementInformationCode(101);//70
			original.setSinarmasReversalInfo("02001442550324142332881881");//90
			original.setAccountIdentification1("6396873100145472");//102
			original.setAccountIdentification2("6396873100145373");//103

			byte[] atBank = original.writeData();
			IsoMessage iso = MsgFactory.parseMessage(atBank, 0);
			SinarmasISOMessage received = new SinarmasISOMessage(iso);
			String str = new String(atBank);

			//			assertEquals(
			//			        str,
			//			        "0200723A4021A8621000166396873100109882300000000000000000042422310816914422310804240424601760388103881166396873100109882191679736190628812400656   SMS SMART                               012191679736190B08232216A6143AA");

			assertEquals(received.getPAN(), original.getPAN());
			assertEquals(received.getProcessingCode(), original.getProcessingCode());
			assertEquals(received.getTransactionAmount(), original.getTransactionAmount());

			Timestamp ts1 = received.getTransmissionTime();
			Timestamp ts2 = original.getTransmissionTime();
			//			assertEquals(received.getTransmissionTime(), original.getTransmissionTime());
			assertEquals(received.getSTAN(), original.getSTAN());
			//			assertEquals(received.getLocalTransactionTime(), original.getLocalTransactionTime());
			//			assertEquals(received.getLocalTransactionDate(), original.getLocalTransactionDate());
			//			assertEquals(received.getSettlementDate(), original.getSettlementDate());
			assertEquals(received.getMerchantType(), original.getMerchantType());
			assertEquals(received.getFunctionCode(), original.getFunctionCode());
			assertEquals(received.getAuthorizingIdentificationResponseLength(), original.getAuthorizingIdentificationResponseLength());
			assertEquals(received.getAcquiringInstitutionIdentificationCode(), original.getAcquiringInstitutionIdentificationCode());
			assertEquals(received.getForwardInstitutionIdentificationCode(), original.getForwardInstitutionIdentificationCode());
			assertEquals(received.getTrack2Data(), original.getTrack2Data());
			assertEquals(received.getRRN(), received.getRRN());
			assertEquals(received.getCardAcceptorTerminalIdentification(), original.getCardAcceptorTerminalIdentification());
			assertEquals(received.getCardAcceptorIdentificationCode(), original.getCardAcceptorIdentificationCode());
			assertEquals(received.getCardAcceptorNameLocation(), original.getCardAcceptorNameLocation());
			assertEquals(received.getPrivateTransactionID(), original.getPrivateTransactionID());
			assertEquals(received.getEncryptedPin(), original.getEncryptedPin());
			assertEquals(received.getAdditionalAmounts(), original.getAdditionalAmounts());
			assertEquals(received.getLastTransactions(), original.getLastTransactions());
			assertEquals(received.getSinarmasReversalInfo(), original.getSinarmasReversalInfo());
			assertEquals(received.getAccountIdentification1(), original.getAccountIdentification1());
			assertEquals(received.getAccountIdentification2(), original.getAccountIdentification2());
		}
		catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		catch (ParseException ex) {
			ex.printStackTrace();
		}
	}
}
