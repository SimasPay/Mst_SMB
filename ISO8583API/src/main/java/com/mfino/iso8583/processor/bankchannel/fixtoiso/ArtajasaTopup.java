package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelResponse;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.ArtajasaISOMessage;

public class ArtajasaTopup implements IFIXtoISOProcessor {

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {

		CMBankChannelResponse response = (CMBankChannelResponse) fixmsg;

		ArtajasaISOMessage isoMsg = getIsoMessage();

		if (CmFinoFIX.ISO8583_ResponseCode_Success.equals(response.getISO8583_ResponseCode()))
			return isoMsg;

		String providerData = null;
		String productIndicator = null;
		productIndicator = isoMsg.getBillingProvidertData().substring(0, 4);

		String paddedVoucherNo = response.getPaymentVoucherNumber().trim();
		paddedVoucherNo = WrapperISOMessage.padOnLeft(paddedVoucherNo, '0', 16);

		if (!productIndicator.equals(CmFinoFIX.ISO8583_Artajasa_ProductIndicator_PostpaidPaymentFren) && !productIndicator.equals(CmFinoFIX.ISO8583_Artajasa_ProductIndicator_PostpaidPaymentHepi)) {
			providerData = String.format("%s%08d%s", isoMsg.getBillingProvidertData().substring(0, 29), response.getPaymentVoucherPeriodYYYYMMDD(), paddedVoucherNo);
		}
		else {
			//			//Format date for M-8 MDN
			String M8DDMMYYY = null;
			M8DDMMYYY = String.format("%08d", response.getLastBillPaymentDateYYYYMMDD());
			String copyDDMMYYY = M8DDMMYYY;
			if (copyDDMMYYY.length() > 1) 
				M8DDMMYYY = String.format("%s%s%s", copyDDMMYYY.substring(6, 8), copyDDMMYYY.substring(4, 6), copyDDMMYYY.substring(0, 4));
			else
				M8DDMMYYY = String.format("%08s", "0");
			providerData = String.format("%s%s%s", isoMsg.getBillingProvidertData().substring(0, 29), M8DDMMYYY, paddedVoucherNo);
		}
		providerData = providerData.toUpperCase();
		isoMsg.setBillingProvidertData(providerData);
		//FIXME ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue());
		return isoMsg;
	}

	public ArtajasaISOMessage getIsoMessage() {
		return null;
	}
}
