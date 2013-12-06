package com.mfino.fidelity.iso8583;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fidelity.iso8583.utils.DateTimeFormatter;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.iso.jpos.util.BaseISOUtil;
import com.mfino.mce.iso.jpos.util.ISOUtil;
import com.mfino.util.DateTimeUtil;

/**
 * @author Sasi
 *
 */
public class FidelityISOUtil extends BaseISOUtil implements ISOUtil{

	
	@Override
	public ISOMsg getEchoMessage() throws ISOException {
		Timestamp ts = DateTimeUtil.getLocalTime();
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setMTI("1804");
		//stan is static, this might cause one in 1 transaction with the same stan fail
		// probability of this happenning is very low so we can live with this for now
		isoMsg.set(11,"000000999999");//STAN
		isoMsg.set(12, DateTimeFormatter.getYYYYMMDDhhmmss(ts)); //12
		isoMsg.set(24,"831"); //functioncode
		isoMsg.set(59, "Echo"); //Transport Data
//		isoMsg.set(93, "102"); //Transaction destination Institution identification code
//		isoMsg.set(94, "102"); //Transaction originator  Institution identification code
		isoMsg.set(123,"MFS"); //Delivery channel controller Id
		return isoMsg;	
	}

	@Override
	public ISOMsg getSignOffMessage() {
		ISOMsg signOff = new ISOMsg();
		return signOff;
	}

	@Override
	public ISOMsg getSignOnMessage() throws ISOException {
		// no signOn for fidelity but send echo as signOn 
		Timestamp ts = DateTimeUtil.getLocalTime();
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setMTI("1804");
		//stan is static, this might cause one in 1 transaction with the same stan fail
		// probability of this happenning is very low so we can live with this for now
		isoMsg.set(11,"000000999998");//STAN
		isoMsg.set(12, DateTimeFormatter.getYYYYMMDDhhmmss(ts)); //12
		isoMsg.set(24,"831"); //functioncode
		isoMsg.set(59, "EchoasSignOn"); //Transport Data
//		isoMsg.set(93, "102"); //Transaction destination Institution identification code
//		isoMsg.set(94, "102"); //Transaction originator  Institution identification code
		isoMsg.set(123,"MFS"); //Delivery channel controller Id
		return isoMsg;	
	}
	
	public String getSignOnSuccessResponseCode() {
		return "800";
	}
	
	public String getEchoSuccessResponseCode() {
		return "800";
	}
	
	public String getSuccessResponseCode() {
		return "000";
	}
}


