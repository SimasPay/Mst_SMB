package com.mfino.iso8583.processor.bankchannel.isotofix;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankChannelEMoneyInquiry;
import com.mfino.iso8583.processor.bankchannel.IVirtualHostRequestProcessor;
import com.mfino.iso8583.processor.bankchannel.isomessages.UMGVHISOMessage;

public class EMoneyTransferInquiry extends BankChannelRequestProcessor implements IVirtualHostRequestProcessor {
	private static Logger log = LoggerFactory.getLogger(EMoneyTransferInquiry.class);

	@Override
	public CFIXMsg process(UMGVHISOMessage isoMsg) throws Exception {
		CMBankChannelEMoneyInquiry request = new CMBankChannelEMoneyInquiry();
		Integer merchantPrefixCode = null;
		if (!StringUtils.isBlank(isoMsg.getAccountIdentification2())) {
			merchantPrefixCode = Integer.parseInt(isoMsg.getAccountIdentification2().substring(0, 4).trim());
			request.setMerchantPrefixCode(merchantPrefixCode);
		}
		request.setBankCode(Integer.parseInt(isoMsg.getForwardInstitutionIdentificationCode()));

		if (merchantPrefixCode > 0) {
			return forwardEmoneyQueryBankChannelRequest(isoMsg, request);
		}
		else {
			return null;
		}

	}

}
