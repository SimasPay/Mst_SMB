package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelResponse;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.UMGVHISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public class UMGVHPaymentReversal implements IFIXtoISOProcessor {

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		
		UMGVHISOMessage isoMsg = getISOMessage();
		CMBankChannelResponse response = (CMBankChannelResponse)fixmsg;
		boolean isAdviceReversal = false;

		if(CmFinoFIX.ISO8583_ResponseCode_Success.equals(response.getISO8583_ResponseCode())) 
			isAdviceReversal = true;

		//FIXME ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue(), isAdviceReversal);
		return isoMsg;
	}
	
	private UMGVHISOMessage getISOMessage() {
		return null;
	}

}
