package com.mfino.iso8583.processor.bankchannel.isotofix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankChannelQueryRequest;
import com.mfino.iso8583.processor.bankchannel.IArtajasaRequestProcessor;
import com.mfino.iso8583.processor.bankchannel.IMobile8RequestProcessor;
import com.mfino.iso8583.processor.bankchannel.IXLinkReqeustProcessor;
import com.mfino.iso8583.processor.bankchannel.isomessages.ArtajasaISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.Mobile8ISOMessage;
import com.mfino.iso8583.processor.bankchannel.isomessages.XLinkISOMessage;

public class Query extends BankChannelRequestProcessor implements IArtajasaRequestProcessor, IXLinkReqeustProcessor, IMobile8RequestProcessor {

	private static Logger log = LoggerFactory.getLogger(Query.class);
	@Override
	public CFIXMsg process(ArtajasaISOMessage isoMsg) throws Exception {
		
		CMBankChannelQueryRequest request = new CMBankChannelQueryRequest();
		request.setSourceMDN(null);//SourceMDNForPostpaid()
		return forwardArtajasaBankChannelRequest(isoMsg, request);
	}
	@Override
	public CFIXMsg process(Mobile8ISOMessage isoMsg) throws Exception {
		CMBankChannelQueryRequest request = new CMBankChannelQueryRequest();
		request.setSourceMDN(null);//SourceMDNForPostpaid()
		return forwardMobile8BankChannelRequest(isoMsg, request);
	}
	@Override
	public CFIXMsg process(XLinkISOMessage isoMsg) throws Exception {
		CMBankChannelQueryRequest request = new CMBankChannelQueryRequest();
		request.setSourceMDN(null);//SourceMDNForPostpaid()
		return forwardXLinkBankChannelRequest(isoMsg, request);
	}

}
