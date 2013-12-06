package com.mfino.iso8583.processor.bankchannel.isotofix;

import java.math.BigDecimal;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankChannelTopupRequest;
import com.mfino.iso8583.processor.bankchannel.IArtajasaRequestProcessor;
import com.mfino.iso8583.processor.bankchannel.IMobile8RequestProcessor;
import com.mfino.iso8583.processor.bankchannel.IXLinkReqeustProcessor;
import com.mfino.iso8583.processor.bankchannel.isomessages.ArtajasaISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.Mobile8ISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public class Topup extends BankChannelRequestProcessor implements IArtajasaRequestProcessor, IMobile8RequestProcessor, IXLinkReqeustProcessor {
	@Override
	public CFIXMsg process(ArtajasaISOMessage isoMsg) throws Exception {
		
		CMBankChannelTopupRequest request = new CMBankChannelTopupRequest();
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()));
		request.setSourceMDN(null);//SourceMDNForPrepaid()
		
		if(request.getAmount().longValue()>0)
			return forwardArtajasaBankChannelRequest(isoMsg, request);
		else
			return null;//ForwardResponse(ISOMsg,"13");
	}

	@Override
	public CFIXMsg process(Mobile8ISOMessage isoMsg) throws Exception {
		CMBankChannelTopupRequest request = new CMBankChannelTopupRequest();
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()));
		request.setSourceMDN(null);//SourceMDNForPrepaid()
//		
		if(CmFinoFIX.ISO8583_ProcessingCode_Mobile8_Voucher_Purchase.equals(isoMsg.getProcessingCode().toString())) {
			String amount = isoMsg.getBillingProvidertData().substring(14, 26).trim();
			request.setAmount(new BigDecimal(amount));
		}
		
		if(request.getAmount().longValue()>0)
			return forwardMobile8BankChannelRequest(isoMsg, request);
		else
			return null;//ForwardResponse(ISOMsg,"13");
	}

	@Override
	public CFIXMsg process(XLinkISOMessage isoMsg) throws Exception {
		CMBankChannelTopupRequest request = new CMBankChannelTopupRequest();
		request.setAmount(new BigDecimal(isoMsg.getTransactionAmount()));
		request.setSourceMDN(null);//SourceMDNForPrepaid()
		
		if(request.getAmount().longValue()>0)
			return forwardXLinkBankChannelRequest(isoMsg, request);
		else
			return null;//ForwardResponse(ISOMsg,"13");
	}

}
