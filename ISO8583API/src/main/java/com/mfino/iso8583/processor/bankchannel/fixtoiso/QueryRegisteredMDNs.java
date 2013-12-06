package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelResponse;
import com.mfino.fix.CmFinoFIX.CMQueryRegisteredMDNs;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public class QueryRegisteredMDNs implements IFIXtoISOProcessor {

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		
		XLinkISOMessage isoMsg = getISOMessage();
		CMQueryRegisteredMDNs response = (CMQueryRegisteredMDNs)fixmsg;
//		if(Response.GetLowBalRegisteredMDNsPtr())
//			pISOMsg->SetBillingProviderData(Response.GetLowBalRegisteredMDNsValue());
//	
		if(CmFinoFIX.ISO8583_ResponseCode_Success.equals(response.getISO8583_ResponseCode()))
			if(response.getLowBalRegisteredMDNs()!=null)
				isoMsg.setBillingProvidertData(response.getLowBalRegisteredMDNs());

		//FIXME ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue(), isAdviceReversal);
		return isoMsg;
	}
	
	private XLinkISOMessage getISOMessage() {
		return null;
	}

}
