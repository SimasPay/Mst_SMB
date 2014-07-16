package com.mfino.bsm.iso8583.processor.isotofix;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.iso8583.processor.BSMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentFromBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class BillPaymentFromBankProcessor implements BSMISOtoFixProcessor{

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {

		CMBSIMBillPaymentToBank toBank = (CMBSIMBillPaymentToBank)request;
		CMBSIMBillPaymentFromBank fromBank = new CMBSIMBillPaymentFromBank();
        
		fromBank.copy(toBank);
		if(isoMsg.hasField(38))
			fromBank.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			fromBank.setResponseCode(isoMsg.getString(39));
		if(isoMsg.hasField(48))
			fromBank.setBankAccountName(isoMsg.getString(48));
		if(isoMsg.hasField(62))
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
						DE62 = DE62+"|"+response.substring(0,38).trim();
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
			
			fromBank.setInfo1(DE62);
		}
		fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromBank;
	}

}
