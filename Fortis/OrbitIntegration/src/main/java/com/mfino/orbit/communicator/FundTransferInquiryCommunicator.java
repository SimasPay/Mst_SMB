package com.mfino.orbit.communicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class FundTransferInquiryCommunicator  extends OrbitCommunicator{

	private static Logger log = LoggerFactory.getLogger(FundTransferInquiryCommunicator.class);
	
	@Override
	public MCEMessage process(MCEMessage mceMessage) {
		log.info("process():BEGIN");
			CMTransferInquiryToBank toBank = (CMTransferInquiryToBank) mceMessage.getResponse();
			CMTransferInquiryFromBank fromBank = new CMTransferInquiryFromBank();
			fromBank.copy(toBank);
			fromBank.setResponseCode(CmFinoFIX.ISO8583_ResponseCode_Success);
			fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
			fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			mceMessage.setRequest(toBank);
			mceMessage.setResponse(fromBank);
			//teller case
			if(mceMessage.getResponse() instanceof CMBankTellerTransferInquiryToBank){
				CMBankTellerTransferInquiryFromBank tellerFromBank = (CMBankTellerTransferInquiryFromBank) fromBank;
				tellerFromBank.setIsInDirectCashIn(((CMBankTellerTransferInquiryToBank)toBank).getIsInDirectCashIn());
				mceMessage.setResponse(tellerFromBank);
			}
				
		log.info("process():END");
		return mceMessage;
	}

}
