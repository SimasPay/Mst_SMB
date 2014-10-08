package com.mfino.bsim.iso8583.processor.isotofix;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jpos.iso.ISOMsg;

import com.mfino.bsim.iso8583.processor.BSIMISOtoFixProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class GetSubscriberDetailsFromBankProcessor implements BSIMISOtoFixProcessor {
	
	public static final Integer FIRST_NAME_START_INDEX = 20;
	public static final Integer FIRST_NAME_END_INDEX = 60;
	public static final Integer LAST_NAME_START_INDEX = 80;
	public static final Integer LAST_NAME_END_INDEX = 100;
	public static final Integer EMAIL_START_INDEX = 160;
	public static final Integer EMAIL_END_INDEX = 200;
	public static final Integer DOB_START_INDEX = 200;
	public static final Integer DOB_END_INDEX = 208;

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
		if(FIRST_NAME_END_INDEX <= de48.length()){
			String firstName = de48.substring(FIRST_NAME_START_INDEX,FIRST_NAME_END_INDEX);
			response.setFirstName(firstName.trim());
		}
//		String midName = de48.substring(60,80);
		if(LAST_NAME_END_INDEX <= de48.length()) {
			String lastName= de48.substring(LAST_NAME_START_INDEX,LAST_NAME_END_INDEX);
			response.setLastName(lastName.trim());
		}
		if(EMAIL_END_INDEX <= de48.length()) {
			String email = de48.substring(EMAIL_START_INDEX,EMAIL_END_INDEX);
			response.setEmail(email.trim());
		}
		if(DOB_END_INDEX <= de48.length()) {
			String dob = de48.substring(DOB_START_INDEX,DOB_END_INDEX);
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			java.util.Date date;
			try {
				date = sdf.parse(dob);
				Timestamp ts = new Timestamp(date.getTime());
				response.setDateOfBirth(ts);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
			}
		}
		//String motherMaidenName = de48.substring(208,238);
		//response.setMothersMaidenName(motherMaidenName);
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
