package com.mfino.bsm.iso8583;

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
public class BSMISOUtil extends BaseISOUtil implements ISOUtil {

	@Override
	public ISOMsg getSignOnMessage() throws ISOException {
        Timestamp ts = DateTimeUtil.getLocalTime();
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setMTI("0800");
        isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); //7
        //stan is static, this might cause one in 1 transaction with the same stan fail
        // probability of this happenning is very low so we can live with this for now
        isoMsg.set(11,"000001");//11
//        isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); //12
//        isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); //13
        isoMsg.set(33,"881");
        isoMsg.set(70,"001"); //70
        return isoMsg;
	}

	@Override
	public ISOMsg getEchoMessage() throws ISOException {
        ISOMsg isoMsg = new ISOMsg();
        Timestamp ts = DateTimeUtil.getLocalTime();
        isoMsg.setMTI("0800");
        isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); //7
        //stan is static, this might cause one in 1L  transaction with the same stan fail
        // probability of this happening is very low so we can live with this for now
        isoMsg.set(11,"999998");//11
//        isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); //12
//        isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); //13
        isoMsg.set(33,"881");
        isoMsg.set(70,"301"); //70
        return isoMsg;
	}

	@Override
	public ISOMsg getSignOffMessage() {
		ISOMsg signOff = new ISOMsg();
		return signOff;
	}
}
