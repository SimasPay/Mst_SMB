package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelResponse;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.Mobile8ISOMessage;

public class Mobile8Query implements IFIXtoISOProcessor {

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {

		CMBankChannelResponse response = (CMBankChannelResponse) fixmsg;

		Mobile8ISOMessage isoMsg = getIsoMessage();

		if (CmFinoFIX.ISO8583_ResponseCode_Success.equals(response.getISO8583_ResponseCode()))
			return isoMsg;

		String providerData = null;
		String M8DDMMYYYY;
		M8DDMMYYYY = String.format("%d", response.getLastBillPaymentDateYYYYMMDD());
		String copyM8 = M8DDMMYYYY;
		if (copyM8.length() > 1) {
			M8DDMMYYYY = String.format("%s%s%s", copyM8.substring(6, 8), copyM8.substring(4, 6), copyM8.substring(0, 4));
		}
		else
			M8DDMMYYYY = String.format("%08s", "0");

		providerData = String.format("%s%-16d%-30s%012d%s", isoMsg.getBillingProvidertData().substring(0, 14), response.getBillReferenceNumber(), response.getPayerName().substring(0, 30),
		        response.getTotalBillDebts(), M8DDMMYYYY);

		providerData = providerData.toUpperCase();
		isoMsg.setBillingProvidertData(providerData);
		//FIXME ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue());
		return isoMsg;
	}

	public Mobile8ISOMessage getIsoMessage() {
		return null;
	}
}
