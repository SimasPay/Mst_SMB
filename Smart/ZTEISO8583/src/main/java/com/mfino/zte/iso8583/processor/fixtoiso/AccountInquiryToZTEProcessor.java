package com.mfino.zte.iso8583.processor.fixtoiso;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.zte.iso8583.utils.DateTimeFormatter;
import com.mfino.zte.iso8583.utils.StringUtilities;

public class AccountInquiryToZTEProcessor extends MobileOperatorRequestProcessor {

	public AccountInquiryToZTEProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			log.error("Error:",ex);
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		try{
			CMGetMDNBillDebtsToOperator toOperator = (CMGetMDNBillDebtsToOperator) fixmsg;
			Timestamp ts = DateTimeUtil.getLocalTime();
			Long transactionID = toOperator.getParentTransactionID();
			transactionID = transactionID % 1000000;
			
			isoMsg.set(3,CmFinoFIX.ISO8583_Mobile_Operator_ProcessingCode_Postpaid_Inquiry);			
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts));
			
			if (toOperator.getISO8583_SystemTraceAuditNumber() != null)
				isoMsg.set(11,toOperator.getISO8583_SystemTraceAuditNumber());
			else
				isoMsg.set(11,transactionID.toString());
			
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); 
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); 
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); 
			
/*			if (toOperator.getISO8583_MerchantType() != null)
				isoMsg.set(18,toOperator.getISO8583_MerchantType());
			
			else
				isoMsg.set(18,FixToISOUtil.GetMerchantTypeBySourceApplication(toOperator.getSourceApplication()));
*/					
			isoMsg.set(18,constantFieldsMap.get("18"));
			
			if (toOperator.getISO8583_AcquiringInstIdCode() != null)
				isoMsg.set(32,toOperator.getISO8583_AcquiringInstIdCode().toString());
			else
				isoMsg.set(32,CmFinoFIX.ISO8583_AcquiringInstIdCode_mFino_To_Smart.toString());
		
			if (toOperator.getISO8583_RetrievalReferenceNum() != null)
				isoMsg.set(37,toOperator.getISO8583_RetrievalReferenceNum());
			else
				isoMsg.set(37,transactionID.toString());

			isoMsg.set(43,"SMS SMART");
			
			if (StringUtils.isBlank(toOperator.getProductIndicatorCode()))
				isoMsg.set(48,"5999");
			else
				isoMsg.set(48,toOperator.getProductIndicatorCode());
			
			isoMsg.set(49,CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_IDR.toString());
			isoMsg.set(61, StringUtilities.padWithSpaces(toOperator.getDestMDN(), 13));
		}catch (ISOException ex) {
			log.error("AccountInquiryToZTEProcessor :Error",ex);
		}
		
		return isoMsg;
	}

}