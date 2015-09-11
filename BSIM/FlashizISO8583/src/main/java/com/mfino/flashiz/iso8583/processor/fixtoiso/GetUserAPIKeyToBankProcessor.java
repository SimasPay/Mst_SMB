package com.mfino.flashiz.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyToBank;
import com.mfino.flashiz.iso8583.utils.DateTimeFormatter;
import com.mfino.flashiz.iso8583.utils.StringUtilities;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

public class GetUserAPIKeyToBankProcessor extends BankRequestProcessor {

	public GetUserAPIKeyToBankProcessor() {
		try {
			isoMsg.setMTI("0800");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		// super.process(fixmsg);
		CMGetUserAPIKeyToBank request = (CMGetUserAPIKeyToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
		Timestamp localTS = DateTimeUtil.getLocalTime();
        try {
        	String mpan = MfinoUtil.CheckDigitCalculation(request.getSourceMDN());
			isoMsg.set(3, "000010");
            isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts));
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));
			isoMsg.set(32,constantFieldsMap.get("32"));
			isoMsg.set(33,constantFieldsMap.get("33"));
       }
		catch (ISOException ex) {

		}	
		return isoMsg;
	}

//	public static void main(String[] args) throws Exception {
//
//		Timestamp ts = new Timestamp();
//		System.out.println(String.format("%Tm%<Td%<TH%<TM%<TS", ts));
//		System.out.println(String.format("%Tm%<Td", ts));
//		System.out.println(String.format("%Ty%<Tm", ts));
//		System.out.println(String.format("%TH%<TM%<TS", ts));
//		System.out.println(String.format("%TC%<Ty%<Tm%<Td", ts));
//
//		CMBalanceInquiryToBank msg = new CMBalanceInquiryToBank();
//		msg.setSourceCardPAN("55555555");
//		msg.setSourceBankAccountType(CmFinoFIX.BankAccountType_Saving.toString());
//		msg.setTransactionID(12345l);
//
//		GetUserAPIKeyToBankProcessor toZ = new GetUserAPIKeyToBankProcessor();
//		ISOMsg isoMsg = toZ.process(msg);
//
//		ISOPackager packager = new PostPackager();
//		isoMsg.setPackager(packager);
//		System.out.println(new String(isoMsg.pack()));
//
//	}

}