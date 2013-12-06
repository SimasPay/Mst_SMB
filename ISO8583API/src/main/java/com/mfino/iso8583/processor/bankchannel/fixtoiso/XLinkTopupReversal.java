package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelResponse;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public class XLinkTopupReversal implements IFIXtoISOProcessor {

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {

		CMBankChannelResponse response = (CMBankChannelResponse) fixmsg;

		XLinkISOMessage isoMsg = getIsoMessage();

		if (CmFinoFIX.ISO8583_ResponseCode_Success.equals(response.getISO8583_ResponseCode()))
			return isoMsg;
		String providerData = null;
		providerData = String.format("%-16s%-16s%08d", isoMsg.getXLinkPrivatRequestData().substring(0, 14),response.getPaymentVoucherNumber(), response.getPaymentVoucherPeriodYYYYMMDD());
		isoMsg.setXLinkPrivatResponseData(providerData);
		isoMsg.setTransactionResponseData(providerData);
//		ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue(), isAdviceReversal);
		return isoMsg;

	}
	public XLinkISOMessage getIsoMessage() {
		return null;
	}

}
