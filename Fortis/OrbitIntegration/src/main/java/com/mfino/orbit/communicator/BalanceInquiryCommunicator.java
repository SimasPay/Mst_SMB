package com.mfino.orbit.communicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.orbit.OrbitConstants;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;
import com.techzultants.orbit.ws.AccountBalanceBean;

public class BalanceInquiryCommunicator extends OrbitCommunicator {

	private static Logger log = LoggerFactory.getLogger(BalanceInquiryCommunicator.class);

	@Override
	public MCEMessage process(MCEMessage mceMessage) {
		log.info("process():BEGIN");
		CMBalanceInquiryToBank toBank= (CMBalanceInquiryToBank) mceMessage.getResponse();
		CMBalanceInquiryFromBank fromBank = new CMBalanceInquiryFromBank();
		fromBank.copy(toBank);
		
		try{	
			log.info("sending BalanceInquiry request for Account:"+ toBank.getSourceCardPAN());
			AccountBalanceBean balanceDetails = orbitService.getAccountBalance(toBank.getSourceCardPAN());
			
			if (balanceDetails != null) {
				log.info("reply received for BalanceInquiry request for Account:"
						+ toBank.getSourceCardPAN()
						+ " responseCode:"
						+ balanceDetails.getResponseCode());
				fromBank.setResponseCode(balanceDetails.getResponseCode());
				
				if (OrbitConstants.SUCCESS.equals(balanceDetails.getResponseCode())) {
					fromBank.setResponseCode(CmFinoFIX.ISO8583_ResponseCode_Success);
					CGEntries[] entries = fromBank.allocateEntries(2);
					entries[0] = new CGEntries();
					entries[1] = new CGEntries();
					entries[0].setAmount(balanceDetails.getLedgerBalance());
					entries[1].setAmount(balanceDetails.getAvailableBalance());
					// set mandatory fields
					entries[0].setCurrency(CmFinoFIX.Currency_NGN);
					entries[0].setBankAmountType(1);
					entries[0].setBankAccountType(1);
					entries[1].setCurrency(CmFinoFIX.Currency_NGN);
					entries[1].setBankAmountType(1);
					entries[1].setBankAccountType(1);
				}
			}else{
				log.info("Empty reponse received");
				fromBank.setResponseCode(OrbitConstants.SYSERROR); //set failure response
			}
		} catch (Exception e) {
			fromBank.setResponseCode(handleWSCommunicationException(e));
		}
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		mceMessage.setRequest(toBank);
		mceMessage.setResponse(fromBank);
		log.info("process():END");
		return mceMessage;
	}

}
