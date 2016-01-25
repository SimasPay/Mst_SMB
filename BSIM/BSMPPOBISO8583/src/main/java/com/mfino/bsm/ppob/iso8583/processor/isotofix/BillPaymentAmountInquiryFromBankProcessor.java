package com.mfino.bsm.ppob.iso8583.processor.isotofix;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.ppob.iso8583.processor.BSMPPOBISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountFromBiller;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountToBiller;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class BillPaymentAmountInquiryFromBankProcessor implements BSMPPOBISOtoFixProcessor{
	
	@Override
    public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		CMBSIMGetAmountToBiller toBank = (CMBSIMGetAmountToBiller)request;
		CMBSIMGetAmountFromBiller fromBank = new CMBSIMGetAmountFromBiller();
		 long amount = 0;
		
		fromBank.copy(toBank);
		if(isoMsg.hasField(3))
			fromBank.setProcessingCode(isoMsg.getString(3).substring(4, 6));
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		if(isoMsg.hasField(4))
			amount = Long.parseLong(isoMsg.getString(4))/100;
		BigDecimal amt = new BigDecimal(amount);
		fromBank.setAmount(amt);
	
		if(isoMsg.hasField(62) && fromBank.getResponseCode().equals(CmFinoFIX.ISO8583_ResponseCode_Success))
		{
			String response = "";
			int totalRows = 0;
			if(isoMsg.getString(62).length() >=2 ){
				totalRows = Integer.valueOf(isoMsg.getString(62).substring(0, 2));
				response = isoMsg.getString(62).substring(2);
			}
			
			String DE62 = "";
			
			for(int i=0; i<totalRows;i++){
				if(response.length() >= 38){
					if(StringUtils.isNotBlank(response.trim())){
						DE62 = DE62+"|"+response.substring(0,38);
					}	
					response = response.substring(38);
				}
				else if(response.length() > 0){
					if(StringUtils.isNotBlank(response.trim())){
						DE62 = DE62 + "|" + response;
						response = "";
					}
				}
			}
			fromBank.setAdditionalInfo(isoMsg.getString(62));
			fromBank.setInfo3(DE62);
		}
				
		fromBank.setInfo1(isoMsg.getString(61));		
		
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		
	    return fromBank;
    }
}
