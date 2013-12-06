package com.mfino.nfc.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.PostPackager;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMNFCCardLinkToCMS;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.nfc.iso8583.utils.DateTimeFormatter;
import com.mfino.nfc.iso8583.utils.StringUtilities;
import com.mfino.util.DateTimeUtil;

public class NFCCardLinkToCMSProcessor extends NFCRequestProcessor {

	public NFCCardLinkToCMSProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		
		CMNFCCardLinkToCMS request = (CMNFCCardLinkToCMS) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();
        try {
        	//String mpan = MfinoUtil.CheckDigitCalculation(request.getSourceMDN());
			String mpan = request.getSourceCardPAN();
        	isoMsg.set(2, mpan);
            isoMsg.set(3, "500000");
            isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString(), 6, "0"));
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); // 12	
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); // 13
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(33,constantFieldsMap.get("32"));// 33
			isoMsg.set(37, StringUtilities.leftPadWithCharacter(request.getTransactionID().toString(), 12,"0"));
			isoMsg.set(42,StringUtilities.leftPadWithCharacter(request.getSourceMDN(),15,""));
			isoMsg.set(43,StringUtilities.leftPadWithCharacter( constantFieldsMap.get("43"),40,""));
			isoMsg.set(48, request.getSourceMDN());
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

		CMNFCCardLinkToCMS msg = new CMNFCCardLinkToCMS();
		msg.setSourceCardPAN("55555555");
		msg.setTransactionID(12345l);

		NFCCardLinkToCMSProcessor toZ = new NFCCardLinkToCMSProcessor();
		ISOMsg isoMsg = toZ.process(msg);

		ISOPackager packager = new PostPackager();
		isoMsg.setPackager(packager);
		System.out.println(new String(isoMsg.pack()));

	}

}