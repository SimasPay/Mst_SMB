package com.mfino.iso8583.processor.zenithbank.fixtoiso.networkmanagement;

import java.io.IOException;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.mfino.util.DateTimeUtil;

public class SignOff implements IFIXtoISOProcessor {

	protected ZenithBankISOMessage isoMsg ;
	
	public SignOff() throws IOException {
		isoMsg = (ZenithBankISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x800,CmFinoFIX.ISO8583_Variant_Zenith_Bank_Interface);
	}
	
	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setTransmissionTime(ts);//7
		isoMsg.setSTAN(getSTAN());//11//FIXME STAN??
		isoMsg.setLocalTransactionTime(ts);//12
		isoMsg.setLocalTransactionDate(ts);//13
		isoMsg.setNetworkManagementInformationCode(002); //70
		
		return isoMsg;
	}
	
	public long getSTAN() {
		return -1l;
	}
}
