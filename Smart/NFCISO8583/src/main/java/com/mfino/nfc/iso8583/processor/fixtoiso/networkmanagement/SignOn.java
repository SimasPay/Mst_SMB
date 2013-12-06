package com.mfino.nfc.iso8583.processor.fixtoiso.networkmanagement;

import java.util.UUID;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.nfc.iso8583.processor.fixtoiso.NFCRequestProcessor;
import com.mfino.nfc.iso8583.utils.DateTimeFormatter;

public class SignOn extends NFCRequestProcessor{

	protected ISOMsg	isoMsg;

	public SignOn() throws Exception {
		isoMsg.setMTI("0800");
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {

		Timestamp ts = DateTimeUtil.getLocalTime();
		try {
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			isoMsg.set(11, "123456"); // 11
			isoMsg.set(33, CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString());//FIXME change it to mfino to zte
			isoMsg.set(70, "001"); // 70
		}
		catch (ISOException ex) {
		}
		System.out.println("ISO message sent:" + isoMsg.toString());
		return isoMsg;
	}

	public long getSTAN() {
		return UUID.randomUUID().getMostSignificantBits();
	}
}
