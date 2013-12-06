package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelResponse;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.Mobile8ISOMessage;

public class Mobile8Topup implements IFIXtoISOProcessor {

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		CMBankChannelResponse response = (CMBankChannelResponse) fixmsg;

		Mobile8ISOMessage isoMsg = getIsoMessage();

		if (CmFinoFIX.ISO8583_ResponseCode_Success.equals(response.getISO8583_ResponseCode()))
			return isoMsg;
		String providerData = null;

		//			//Format date for M-8 MDN
		String M8DDMMYYY = null;
		M8DDMMYYY = String.format("%d", response.getLastBillPaymentDateYYYYMMDD());
		long namount = Long.parseLong(isoMsg.getBillingProvidertData().substring(14, 26).trim());

		String paddedVoucherNo = response.getPaymentVoucherNumber().trim();
		paddedVoucherNo = WrapperISOMessage.padOnLeft(paddedVoucherNo, '0', 14);
		String copyDDMMYYY = M8DDMMYYY;
		if (copyDDMMYYY.length() > 1)
			M8DDMMYYY = String.format("%s%s%s", copyDDMMYYY.substring(6, 8), copyDDMMYYY.substring(4, 6), copyDDMMYYY.substring(0, 4));
		else
			M8DDMMYYY = String.format("%08s", "0");
		providerData = String.format("%-14s%012d%s%s", isoMsg.getBillingProvidertData().substring(0, 14),namount, M8DDMMYYY,paddedVoucherNo);

		providerData = providerData.toUpperCase();
		isoMsg.setBillingProvidertData(providerData);
		//FIXME ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue());
		return isoMsg;

	}

	public Mobile8ISOMessage getIsoMessage() {
		return null;
	}
}
