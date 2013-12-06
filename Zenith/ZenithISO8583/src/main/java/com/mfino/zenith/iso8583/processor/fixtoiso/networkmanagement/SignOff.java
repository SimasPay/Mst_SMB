package com.mfino.zenith.iso8583.processor.fixtoiso.networkmanagement;

import java.util.UUID;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.zenith.iso8583.processor.fixtoiso.ZenithBankRequestProcessor;

public class SignOff extends ZenithBankRequestProcessor {

	protected ISOMsg	isoMsg;

	public SignOff() throws Exception {
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
			isoMsg.set(70, "002"); // 70
		}
		catch (ISOException ex) {

		}

		return isoMsg;
	}

	public long getSTAN() {
		return UUID.randomUUID().getMostSignificantBits();
	}
}
