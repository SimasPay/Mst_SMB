package com.mfino.bsim.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class InterBankTransferInquiryFromBankProcessor implements BSIMISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMInterBankTransferInquiryToBank toBank = (CMInterBankTransferInquiryToBank)request;
		CMInterBankTransferInquiryFromBank fromBank = new CMInterBankTransferInquiryFromBank();
		
		//TODO: BSIM IBT check codes and change appropriately
		fromBank.copy(toBank);
		if(isoMsg.hasField(3))
			fromBank.setProcessingCode(isoMsg.getString(3).substring(4, 6));
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		
		/*if(isoMsg.hasField(48))
		{
	     String destname =  isoMsg.getString(48).substring(0, 30);
	     String destinationbank = isoMsg.getString(48).substring(46, 76);
	     String destinationbankbranch = isoMsg.getString(48).substring(76, 106);
	     fromBank.setDestinationUserName(destname);
		 fromBank.setBankAccountName(destinationbankbranch);
		 fromBank.setAdditionalInfo(isoMsg.getString(48));
		}*/
		if(isoMsg.hasField(48))
		{
			String destname =  "";
			String de48 = isoMsg.getString(48);
			
			if(de48.length() < 30) {
				destname = de48.substring(0,de48.length()).trim();
			}
			else if(de48.length() >= 30) {
				destname = de48.substring(0, 30).trim();
			}
			
			String destinationbank = "";
			
			if(de48.length() >= 76) {
				destinationbank = de48.substring(46, 76).trim();
			}
			else if(de48.length() > 46){
				destinationbank = de48.substring(46,de48.length());
			}
			
			String destinationbankbranch = "";
			if((de48.length()<106)&&(de48.length()>=76)){
				destinationbankbranch = de48.substring(76,de48.length());
			}
			if(de48.length() >= 106){
				destinationbankbranch = de48.substring(76, 106);
			}
			
			fromBank.setDestinationUserName(destname);
			fromBank.setBankAccountName(destinationbankbranch);
			fromBank.setAdditionalInfo(de48);
		}
		
		if(isoMsg.hasField(63)){
			fromBank.setServiceChargeDE63(isoMsg.getString(63));
		}
		
		if(isoMsg.hasField(102)){
			fromBank.setSourceAccountNumber(isoMsg.getString(102));
		}
		
		if(isoMsg.hasField(103)){
			fromBank.setDestAccountNumber(isoMsg.getString(103));
		}
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
	    return fromBank;
	}

}
