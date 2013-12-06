package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankChannelResponse;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public class Payment implements IFIXtoISOProcessor {

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {

		CMBankChannelResponse response = (CMBankChannelResponse)fixmsg;
		XLinkISOMessage isoMsg = getIsoMessage();
		isoMsg.setXLinkPrivatResponseData(isoMsg.getXLinkPrivatRequestData());
	
		//ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue());
		return isoMsg;
	}
	
	public XLinkISOMessage getIsoMessage() {
		return null;
	}

}
