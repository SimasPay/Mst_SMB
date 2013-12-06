package com.mfino.gt.iso8583.processor.fixtoiso.networkmanagement;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.gt.iso8583.processor.fixtoiso.GTBankRequestProcessor;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.util.DateTimeUtil;

public class EchoTestResponse extends GTBankRequestProcessor {

	protected ISOMsg	isoMsg;

	public EchoTestResponse() throws Exception {
		isoMsg.setMTI("0800");
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		Timestamp ts = DateTimeUtil.getLocalTime();
		try {
			isoMsg.set(7, ts.toString()); // 7
			isoMsg.set(11, "123456"); // 11
			isoMsg.set(12, ts.toString()); // 12
			isoMsg.set(13, ts.toString()); // 13
			isoMsg.set(39, "00");// 39
			isoMsg.set(70, "301"); // 70
		}
		catch (ISOException ex) {

		}

		return isoMsg;
	}
}
