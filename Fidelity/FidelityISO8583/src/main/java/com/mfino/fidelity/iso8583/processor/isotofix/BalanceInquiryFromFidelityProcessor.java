package com.mfino.fidelity.iso8583.processor.isotofix;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.fidelity.iso8583.GetConstantCodes;
import com.mfino.fidelity.iso8583.processor.FidelityISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class BalanceInquiryFromFidelityProcessor implements FidelityISOtoFixProcessor {
	
	public Log log = LogFactory.getLog(this.getClass());
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CmFinoFIX.CMBalanceInquiryFromBank response = new CmFinoFIX.CMBalanceInquiryFromBank();
		CMBalanceInquiryToBank toBank = (CMBalanceInquiryToBank) request;

		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
		
		response.copy(toBank);
		if(isoMsg.hasField(38))
			response.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			response.setResponseCode(isoMsg.getString(39));
		
		//if the response is failure there is any point trying to parse the result
		if(GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		{
			response.setResponseCode(CmFinoFIX.ISO8583_ResponseCode_Success);
			String amounts = isoMsg.getString(48);
			// entry size is always 17 with feild being 
			CGEntries[] entries = response.allocateEntries(5);
			int index = 0; 
			int entrysize =17; 
			String currencyCode = amounts.substring(85, 88);
			log.info("BalanceInquiryFromFidelityProcessor:Amount:"+amounts);
			while (index < 5) {
				entries[index] = new CmFinoFIX.CMBalanceInquiryFromBank.CGEntries();	
				String checkbalanceContent = amounts.substring(index*entrysize,(index+1)*entrysize);
				 try {
					 BigDecimal amount = new BigDecimal(checkbalanceContent.substring(1,17)).divide(new BigDecimal(100));
					 entries[index].setAmount(amount);
					 entries[index].setCurrency(currencyCode);
					 entries[index].setBankAmountType(1);// fix this
					 entries[index].setBankAccountType(1);// fix this
					if (checkbalanceContent.substring(0, 1) == "-")
						entries[index].setAmount(entries[index].getAmount().negate());
					
                }
                catch (Exception ex) {
	                ex.printStackTrace();
	                InvalidIsoElementException exception = new InvalidIsoElementException("48");
	                exception.fillInStackTrace();
	                throw exception;
                }			
				index++;
			}
		}
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
