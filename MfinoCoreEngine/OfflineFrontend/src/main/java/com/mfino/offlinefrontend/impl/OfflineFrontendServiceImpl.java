package com.mfino.offlinefrontend.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankRequest;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.offlinefrontend.OfflineFrontendService;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class OfflineFrontendServiceImpl implements OfflineFrontendService {

	private Log	log	= LogFactory.getLog(this.getClass());
	
	@Override
	public MCEMessage processMessage(MCEMessage msg) {
		log.info("processing in offline bank message");

		log.info("MCERequest--> " + msg.getRequest().getClass());
		log.info("MCEResponse-->" + msg.getResponse().getClass());
		
		CFIXMsg request = msg.getResponse();
		
		MCEMessage mceMessageResponse = new MCEMessage();
		mceMessageResponse.setRequest(request);
		
		if (request instanceof CMTransferInquiryToBank) {
			CMTransferInquiryFromBank fromBank = new CMTransferInquiryFromBank();
			// As there is no bank communication we are setting the dummy AIR code and ResponseCode as '00' to make Inquiry as Success.
			fromBank.copy((CMTransferInquiryToBank)request);
			fromBank.setAIR("123456"); 
			fromBank.setResponseCode("00");
			fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
			fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
			mceMessageResponse.setResponse(fromBank);
		}
		else {
			NoISOResponseMsg noResponse = new NoISOResponseMsg();
			noResponse.copy((CMBankRequest) msg.getResponse());
			noResponse.header().setSendingTime(DateTimeUtil.getLocalTime());
			noResponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			mceMessageResponse.setResponse(noResponse);
		}
		log.info("Response message class name--> " + mceMessageResponse.getResponse().getClass().getName());
		return mceMessageResponse;
	}

}
