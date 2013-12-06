package com.mfino.bsim.iso8583.processor.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class TransferInquiryFromBankProcessor implements BSIMISOtoFixProcessor{
	
	@Override
    public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMTransferInquiryToBank toBank = (CMTransferInquiryToBank)request;
		CMTransferInquiryFromBank fromBank = new CMTransferInquiryFromBank();
		
		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, fromBank);
		
		
		fromBank.copy(toBank);
		if(isoMsg.hasField(3))
			fromBank.setProcessingCode(isoMsg.getString(3).substring(4, 6));
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		if(isoMsg.hasField(48))
		{
			String destname =  "";
			
			if(isoMsg.getString(48).length() < 30) {
				destname = isoMsg.getString(48).substring(0,isoMsg.getString(48).length());
			}
			else if(isoMsg.getString(48).length() >= 30) {
				destname = isoMsg.getString(48).substring(0, 30);
			}
			
			String destinationbank = "";
			
			if(isoMsg.getString(48).length() >= 76) {
				destinationbank = isoMsg.getString(48).substring(46, 76);
			}
			
			String destinationbankbranch = "";
			if((isoMsg.getString(48).length()<106)&&(isoMsg.getString(48).length()>=76)){
				destinationbankbranch = isoMsg.getString(48).substring(76,isoMsg.getString(48).length());
			}
			if(isoMsg.getString(48).length() >= 106){
				destinationbankbranch = isoMsg.getString(48).substring(76, 106);
			}
			
			fromBank.setDestinationUserName(destname);
			fromBank.setBankAccountName(destinationbankbranch);

		    fromBank.setAdditionalInfo(isoMsg.getString(48));
            fromBank.setAdditionalInfo(isoMsg.getString(48));
			fromBank.setBankName("Bank Sinarmas");

		}
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
	    return fromBank;
    }

}
