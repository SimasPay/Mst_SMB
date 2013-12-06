package com.mfino.bsim.iso8583.processor.isotofix;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.util.Log;

import com.mfino.bsim.iso8583.AdditionalAmounts;
import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.bsim.iso8583.utils.DateTimeFormatter;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class GetSubscriberDetailsFromBankProcessor implements BSIMISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CMGetSubscriberDetailsFromBank response = new CMGetSubscriberDetailsFromBank();
		CMGetSubscriberDetailsToBank toBank = (CMGetSubscriberDetailsToBank) request;

		//if(!GetConstantCodes.SUCCESS.equals(isoMsg.getString(39)))
		//	return ISOtoFIXProcessor.getGenericResponse(isoMsg, toBank);
		
		response.copy(toBank);
		if(isoMsg.hasField(38))
			response.setAIR(isoMsg.getString(38));
		if(isoMsg.hasField(39))
			response.setResponseCode(isoMsg.getString(39));
		String de48 = isoMsg.getString(48);
		String firstName = de48.substring(20,60);
		response.setFirstName(firstName.trim());
		String midName = de48.substring(60,80);
		String lastName= de48.substring(80,100);
		response.setLastName(lastName.trim());
		String email = de48.substring(160,200);
		response.setEmail(email.trim());
		String dob = de48.substring(200,208);
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		java.util.Date date;
		try {
			date = sdf.parse(dob);
			Timestamp ts = new Timestamp(date.getTime());
			response.setDateOfBirth(ts);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		//String motherMaidenName = de48.substring(208,238);
		//response.setMothersMaidenName(motherMaidenName);
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
