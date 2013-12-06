package com.mfino.bsim.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.PostPackager;

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.bsim.iso8583.utils.StringUtilities;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

public class GetSubscriberDetailsToBankProcessor extends BankRequestProcessor {

	public GetSubscriberDetailsToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		// super.process(fixmsg);
		CMGetSubscriberDetailsToBank request = (CMGetSubscriberDetailsToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
		Timestamp localTS = DateTimeUtil.getLocalTime();
        try {
        	String mpan = MfinoUtil.CheckDigitCalculation(request.getSourceMDN());
			isoMsg.set(2, mpan);
			isoMsg.set(3, "320000");
            isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(localTS)); // 12	
			isoMsg.set(13, DateTimeFormatter.getMMDD(localTS)); // 13
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); // 15
			isoMsg.set(18, CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			//isoMsg.set(32, constantFieldsMap.get("32"));
			//isoMsg.set(33,constantFieldsMap.get("32"));// 33
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(request.getTransactionID().toString(), 12,"0"));
			isoMsg.set(42,StringUtilities.leftPadWithCharacter(request.getSourceMDN(),15,""));
			//isoMsg.set(43,StringUtilities.leftPadWithCharacter( constantFieldsMap.get("43"),40,""));
			isoMsg.set(47, request.getTransactionID().toString());
			isoMsg.set(48,request.getApplicationID());
			/*
			if(null!=request.getLanguage() && request.getLanguage().equals(0))
			   isoMsg.set(121,constantFieldsMap.get("english"));
			else
			   isoMsg.set(121,constantFieldsMap.get("bahasa"));
			isoMsg.set(102, request.getSourceCardPAN());
			*/
       }
		catch (ISOException ex) {

		}	
		return isoMsg;
	}

	public static void main(String[] args) throws Exception {

		Timestamp ts = new Timestamp();
		System.out.println(String.format("%Tm%<Td%<TH%<TM%<TS", ts));
		System.out.println(String.format("%Tm%<Td", ts));
		System.out.println(String.format("%Ty%<Tm", ts));
		System.out.println(String.format("%TH%<TM%<TS", ts));
		System.out.println(String.format("%TC%<Ty%<Tm%<Td", ts));

		CMBalanceInquiryToBank msg = new CMBalanceInquiryToBank();
		msg.setSourceCardPAN("55555555");
		msg.setSourceBankAccountType(CmFinoFIX.BankAccountType_Saving.toString());
		msg.setTransactionID(12345l);

		GetSubscriberDetailsToBankProcessor toZ = new GetSubscriberDetailsToBankProcessor();
		ISOMsg isoMsg = toZ.process(msg);

		ISOPackager packager = new PostPackager();
		isoMsg.setPackager(packager);
		System.out.println(new String(isoMsg.pack()));

	}

}