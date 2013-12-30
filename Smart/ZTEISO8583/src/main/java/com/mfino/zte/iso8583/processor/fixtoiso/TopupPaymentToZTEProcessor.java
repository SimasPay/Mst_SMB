package com.mfino.zte.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_Mobile_Operator_ProcessingCode_Topup;
import static com.mfino.fix.CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_IDR;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.zte.iso8583.utils.DateTimeFormatter;
import com.mfino.zte.iso8583.utils.StringUtilities;

public class TopupPaymentToZTEProcessor extends MobileOperatorRequestProcessor {

	public TopupPaymentToZTEProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			log.error("Error:",ex);
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		// super.process(fixmsg);
		try{
			CMCommodityTransferToOperator toOperator = (CMCommodityTransferToOperator)fixmsg;
			Long transactionID = toOperator.getParentTransactionID();
			transactionID = transactionID % 1000000;
			
			isoMsg.set(3,ISO8583_Mobile_Operator_ProcessingCode_Topup);
			isoMsg.set(4,StringUtilities.leftPadWithCharacter(toOperator.getAmount().longValue() + "", 12, "0"));

			Timestamp ts = DateTimeUtil.getLocalTime();
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); 
			if (toOperator.getISO8583_SystemTraceAuditNumber() != null)
			{
				isoMsg.set(11,StringUtilities.leftPadWithCharacter(toOperator.getISO8583_SystemTraceAuditNumber() + "", 6, "0"));
			}
			else
			{
				isoMsg.set(11,StringUtilities.leftPadWithCharacter(transactionID.toString() + "", 6, "0"));
			}			
			
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); 
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); 
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); 
			
/*			if(!StringUtils.isBlank( toOperator.getISO8583_MerchantType()))
				isoMsg.set(18,toOperator.getISO8583_MerchantType());
			else
				isoMsg.set(18,FixToISOUtil.GetMerchantTypeBySourceApplication(toOperator.getSourceApplication()));
*/			
			//FIXME make this configurable.
			isoMsg.set(18,constantFieldsMap.get("18"));
			
			isoMsg.set(32,constantFieldsMap.get("32"));
			
			if(!StringUtils.isBlank(toOperator.getISO8583_RetrievalReferenceNum()))
			{
				isoMsg.set(37,StringUtilities.leftPadWithCharacter(toOperator.getISO8583_RetrievalReferenceNum() + "", 12, "0"));
			}
			else
			{
				isoMsg.set(37,StringUtilities.leftPadWithCharacter(toOperator.getTransactionID().toString() + "", 12, "0"));
			}
			
			isoMsg.set(41,StringUtilities.rightPadWithCharacter(constantFieldsMap.get("41") + "", 8," "));
			
			isoMsg.set(43,"SMS SMART");
			if(!StringUtils.isBlank(toOperator.getProductIndicatorCode()))
				isoMsg.set(48,toOperator.getProductIndicatorCode());
			else
				isoMsg.set(48,"5100");
			isoMsg.set(49,ISO8583_Sinarmas_CurrencyCode_IDR.toString());
			
			isoMsg.set(61,StringUtilities.padWithSpaces(toOperator.getDestMDN(), 13));
		}catch (ISOException ex) {
			log.error("TopupPaymentToZTEProcessor :Error",ex);
		}
		return isoMsg;
		
	}
}
