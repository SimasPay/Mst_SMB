package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import org.apache.commons.lang.StringUtils;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMQueryRegisteredMDNs;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public class XLinkQueryRegisteredMDNsresponse implements IFIXtoISOProcessor{
	
	public XLinkISOMessage getISOMessage() {
		return null;
	}
	@Override
    public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {

		XLinkISOMessage isoMsg = getISOMessage();
		
		CMQueryRegisteredMDNs response = (CMQueryRegisteredMDNs)fixmsg;
		
		if(!CmFinoFIX.ISO8583_ResponseCode_Success.equals(response.getISO8583_ResponseCode()))
			return isoMsg;
		if(StringUtils.isBlank(response.getLowBalRegisteredMDNs()))
			isoMsg.setBillingProvidertData(response.getLowBalRegisteredMDNs());
		//ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue());
	    return isoMsg;
    }
}
