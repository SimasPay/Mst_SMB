package com.mfino.flashiz.iso8583.processor.isotofix;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.util.Log;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyFromBank;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyToBank;
import com.mfino.flashiz.iso8583.GetConstantCodes;
import com.mfino.flashiz.iso8583.processor.FlashizISOtoFixProcessor;
import com.mfino.flashiz.iso8583.utils.DateTimeFormatter;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class GetUserAPIKeyFromBankProcessor implements FlashizISOtoFixProcessor {

	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CMGetUserAPIKeyFromBank response = new CMGetUserAPIKeyFromBank();
		CMGetUserAPIKeyToBank toBank = (CMGetUserAPIKeyToBank) request;
		response.copy(toBank);
		if(isoMsg.hasField(39))
			response.setResponseCode(isoMsg.getString(39));
		response.setUserAPIKey(isoMsg.getString(48));
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
