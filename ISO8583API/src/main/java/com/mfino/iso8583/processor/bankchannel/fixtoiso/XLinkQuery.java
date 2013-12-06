package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelResponse;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public class XLinkQuery implements IFIXtoISOProcessor {

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {

		CMBankChannelResponse response = (CMBankChannelResponse) fixmsg;
		XLinkISOMessage isoMsg = getIsoMessage();
		if (CmFinoFIX.ISO8583_ResponseCode_Success.equals(response.getISO8583_ResponseCode()))
			return isoMsg;
		String providerData = null;
		providerData = String.format("%-16s%16s%2s%52s%52s%52s%-16d%012d%24s%-40s%d%2s", isoMsg.getXLinkPrivatRequestData().substring(0, 14),
				"","","","","",response.getBillReferenceNumber(),response.getTotalBillDebts(),response.getPayerName().substring(0, 40),
				response.getLastBillPaymentDateYYYYMMDD(),"");
		isoMsg.setXLinkPrivatResponseData(providerData);
		//FIXME ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue());
		return isoMsg;
	}

	public XLinkISOMessage getIsoMessage() {
		return null;
	}
}
