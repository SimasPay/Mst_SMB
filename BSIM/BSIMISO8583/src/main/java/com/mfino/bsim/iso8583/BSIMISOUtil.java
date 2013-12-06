package com.mfino.bsim.iso8583;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.hibernate.Timestamp;
import com.mfino.mce.iso.jpos.util.BaseISOUtil;
import com.mfino.mce.iso.jpos.util.DateTimeFormatter;
import com.mfino.mce.iso.jpos.util.ISOUtil;
import com.mfino.util.DateTimeUtil;

/**
 * @author Sasi
 * 
 */
public class BSIMISOUtil extends BaseISOUtil implements ISOUtil {

	@Override
	public ISOMsg getSignOnMessage() throws ISOException {
		Timestamp ts = DateTimeUtil.getLocalTime();
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setMTI("0800");
		isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
		// stan is static, this might cause one in 1 transaction with the same
		// stan fail
		// probability of this happenning is very low so we can live with this
		// for now
		isoMsg.set(11, "000001");// 11
		isoMsg.set(33, "881");
		isoMsg.set(70, "001"); // 70
		return isoMsg;
	}

	@Override
	public ISOMsg getEchoMessage() throws ISOException {
		ISOMsg isoMsg = new ISOMsg();
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setMTI("0800");
		isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
		// stan is static, this might cause one in 1L transaction with the same
		// stan fail
		// probability of this happening is very low so we can live with this
		// for now
		isoMsg.set(11, DateTimeFormatter.getHHMMSS(ts));// 11
		isoMsg.set(33, "881");
		isoMsg.set(70, "301"); // 70
		return isoMsg;
	}

	@Override
	public ISOMsg getSignOffMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
	public ISOMsg getSignOffMessage() throws ISOException{
		ISOMsg signOff = new ISOMsg();
		Timestamp ts = DateTimeUtil.getLocalTime();
		signOff.setMTI("0800");
		signOff.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
		// stan is static, this might cause one in 1L transaction with the same
		// stan fail
		// probability of this happening is very low so we can live with this
		// for now
		signOff.set(11, "999998");// 11
		signOff.set(33, "881");
		signOff.set(70, "002"); // 70
		return signOff;
	}*/
	
	
}
