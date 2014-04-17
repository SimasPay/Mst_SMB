package com.mfino.nfc.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.nfc.iso8583.GetConstantCodes;
import com.mfino.nfc.iso8583.utils.DateTimeFormatter;
import com.mfino.nfc.iso8583.utils.StringUtilities;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Amar
 *
 */
public class GetLastTrxnsToBankProcessor extends NFCRequestProcessor {

	public GetLastTrxnsToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		super.process(fixmsg);

		CMGetLastTransactionsToBank msg = (CMGetLastTransactionsToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getGMTTime();

		try {
			
			isoMsg.set(2, msg.getSourceCardPAN());
			String processingCode = CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions;
			if(msg.getMaxCount() < 10)
			{
				processingCode = processingCode.substring(0, processingCode.length()-1).concat(msg.getMaxCount().toString());
			}
			else
			{
				processingCode = processingCode.substring(0, processingCode.length()-2).concat(msg.getMaxCount().toString());
			}
			isoMsg.set(3,processingCode);
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts));
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, StringUtilities.leftPadWithCharacter(transactionID.toString(),6 ,"0"));
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts));
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts));
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(33, constantFieldsMap.get("33"));
			isoMsg.set(37, StringUtilities.rightPadWithCharacter(msg.getTransactionID().toString(), 12,"0"));
			isoMsg.set(42, StringUtilities.rightPadWithCharacter(msg.getSourceMDN(),15,""));
			isoMsg.set(43, StringUtilities.rightPadWithCharacter( constantFieldsMap.get("43"),40," "));
			isoMsg.set(48, msg.getSourceMDN());	
			if(msg.getFromDate() != null && msg.getToDate() != null)
			{
				isoMsg.set(61, DateTimeFormatter.getYYYYMMDD(msg.getFromDate())+DateTimeFormatter.getYYYYMMDD(msg.getToDate())+"0"+StringUtilities.leftPadWithCharacter(msg.getPageNumber().toString(), 3, "0"));
			}
			else
			{
				isoMsg.set(61, "00000000"+"00000000"+"0"+StringUtilities.leftPadWithCharacter(msg.getPageNumber().toString(), 3, "0"));
			}
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}
}
