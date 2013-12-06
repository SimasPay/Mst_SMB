package com.mfino.iso8583.processor.bankchannel.isotofix;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelTopupReversalRequest;
import com.mfino.iso8583.processor.bankchannel.IArtajasaRequestProcessor;
import com.mfino.iso8583.processor.bankchannel.IMobile8RequestProcessor;
import com.mfino.iso8583.processor.bankchannel.IUMGVHRequestProcessor;
import com.mfino.iso8583.processor.bankchannel.IXLinkReqeustProcessor;
import com.mfino.iso8583.processor.bankchannel.isomessages.ArtajasaISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.Mobile8ISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.UMGVHISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public class TopupReversal extends BankChannelRequestProcessor implements IArtajasaRequestProcessor, IMobile8RequestProcessor, IXLinkReqeustProcessor, IUMGVHRequestProcessor {
	private static Logger log = LoggerFactory.getLogger(TopupReversal.class);

	@Override
	public CFIXMsg process(UMGVHISOMessage isoMsg) throws Exception {
		boolean isAdviceReversal = true;
		CMBankChannelTopupReversalRequest request = new CMBankChannelTopupReversalRequest();
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()));
		request.setSourceMDN(null);//SourceMDNForPrepaid()
		request.setPaymentTransactionData(isoMsg.getOriginalPaymentTransactionData());
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount().substring(0, 16)));
		if (!StringUtils.isBlank(isoMsg.getAccountIdentification2())) {
			int mpCode = Integer.parseInt(isoMsg.getAccountIdentification2().substring(0, 4));
			request.setMerchantPrefixCode(mpCode);
		}
		if (request.getAmount().longValue() > 0)
			return forwardUMGVHBankChannelRequest(isoMsg, request);
		else
			return null;//FIXME ForwardResponse(ISOMsg,"13", isAdviceReversal);

	}

	@Override
	public CFIXMsg process(XLinkISOMessage isoMsg) throws Exception {
		boolean isAdviceReversal = true;
		CMBankChannelTopupReversalRequest request = new CMBankChannelTopupReversalRequest();
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()));
		request.setSourceMDN(null);//SourceMDNForPrepaid()
		request.setPaymentTransactionData(isoMsg.getOriginalPaymentTransactionData());

		if (request.getAmount().longValue() > 0)
			return forwardXLinkBankChannelRequest(isoMsg, request);
		else
			return null;//FIXME ForwardResponse(ISOMsg,"13", isAdviceReversal);

	}

	@Override
	public CFIXMsg process(Mobile8ISOMessage isoMsg) throws Exception {

		boolean isAdviceReversal = false;
		CMBankChannelTopupReversalRequest request = new CMBankChannelTopupReversalRequest();
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()));
		request.setSourceMDN(null);//SourceMDNForPrepaid()
		request.setPaymentTransactionData(isoMsg.getOriginalPaymentTransactionData());

		if (CmFinoFIX.ISO8583_ProcessingCode_Mobile8_Voucher_Purchase.equals(isoMsg.getProcessingCode().toString())) {
			String amount = isoMsg.getBillingProvidertData().substring(14, 26);
			request.setAmount(new BigDecimal(amount));
		}
		if (request.getAmount().longValue() > 0)
			return forwardMobile8BankChannelRequest(isoMsg, request);
		else
			return null;//FIXME ForwardResponse(ISOMsg,"13", isAdviceReversal);

	}

	@Override
	public CFIXMsg process(ArtajasaISOMessage isoMsg) throws Exception {
		boolean isAdviceReversal = false;
		CMBankChannelTopupReversalRequest request = new CMBankChannelTopupReversalRequest();
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()));
		request.setSourceMDN(null);//SourceMDNForPrepaid()
		request.setPaymentTransactionData(isoMsg.getOriginalPaymentTransactionData());

		if (request.getAmount().longValue() > 0)
			return forwardArtajasaBankChannelRequest(isoMsg, request);
		else
			return null;//FIXME ForwardResponse(ISOMsg,"13", isAdviceReversal);
	}

}
