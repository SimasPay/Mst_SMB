package com.mfino.iso8583.processor.zenithbank.fixtoiso.networkmanagement;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSignOnToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.mfino.util.DateTimeUtil;

public class SignOn implements IFIXtoISOProcessor {

	protected ZenithBankISOMessage isoMsg ;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public SignOn() throws IOException 
	{
		isoMsg = (ZenithBankISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x800,CmFinoFIX.ISO8583_Variant_Zenith_Bank_Interface);
	}
	
	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setTransmissionTime(ts);//7
		isoMsg.setSTAN(((CMSignOnToBank)fixmsg).getTransactionID());//11//FIXME
		isoMsg.setLocalTransactionTime(ts);//12
		isoMsg.setLocalTransactionDate(ts);//13
		isoMsg.setNetworkManagementInformationCode(001); //70
		log.info("ISO message sent:"+isoMsg.toString());
		return isoMsg;
	}
	public long getSTAN() 
	{
		return UUID.randomUUID().getMostSignificantBits();
	}
}
