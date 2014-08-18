package com.mfino.bsm.uangku.iso8583.processor.fixtoiso.networkmanagement;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.uangku.iso8583.processor.fixtoiso.BankRequestProcessor;
import com.mfino.bsm.uangku.iso8583.utils.DateTimeFormatter;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

public class EchoTestResponse extends BankRequestProcessor {

	protected ISOMsg	isoMsg;

	public EchoTestResponse() throws Exception {
		isoMsg.setMTI("0800");
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		Timestamp ts = DateTimeUtil.getLocalTime();
		try {
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			isoMsg.set(11, "123456"); // 11
			isoMsg.set(33, CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString());
			isoMsg.set(39,"00");
			isoMsg.set(70, "301"); // 70
		}
		catch (ISOException ex) {

		}

		return isoMsg;
	}
}
