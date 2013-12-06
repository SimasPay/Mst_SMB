package com.mfino.iso8583.processor.bankchannel.isotofix;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMH2HBankChannelRequest;
import com.mfino.iso8583.processor.bankchannel.IMobile8RequestProcessor;
import com.mfino.iso8583.processor.bankchannel.isomessages.Mobile8ISOMessage;

public class MerchantTopup extends BankChannelRequestProcessor implements IMobile8RequestProcessor {

	private static Logger log = LoggerFactory.getLogger(MerchantTopup.class);

	@Override
	public CFIXMsg process(Mobile8ISOMessage isoMsg) throws Exception {

		CMH2HBankChannelRequest request = new CMH2HBankChannelRequest();
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()));

		//if Mobile-8 read the amount from element - 48
		String amount = isoMsg.getBillingProvidertData().substring(14, 26).trim();
		request.setAmount(new BigDecimal(amount));

		if (request.getAmount().compareTo(BigDecimal.ZERO)>1) 
		{
			return forwardMerchantBankChannelRequest(isoMsg, request);
		}
		else {
			//FIXME create msg with 13
			return null;
		}
		//FIXME link alive if condition iso msg with 7
	}

}
