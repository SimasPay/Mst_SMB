package com.mfino.zte.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_AcquiringInstIdCode_mFino_To_Smart;
import static com.mfino.fix.CmFinoFIX.ISO8583_Mobile_Operator_ProcessingCode_Postpaid_Payment;
import static com.mfino.fix.CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_IDR;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;
import com.mfino.zte.iso8583.WrapperISOMessage;
import com.mfino.zte.iso8583.utils.DateTimeFormatter;
import com.mfino.zte.iso8583.utils.FixToISOUtil;

public class PostpaidBillPaymentReversalToZTEProcessor extends MobileOperatorRequestProcessor{

	public PostpaidBillPaymentReversalToZTEProcessor() {
		try {
			isoMsg.setMTI("0400");
		}
		catch (ISOException ex) {
			log.error("Error:",ex);
		}
	}

	
	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
			
		try{
			CMCommodityTransferReversalToOperator toOperator = (CMCommodityTransferReversalToOperator)fixmsg;
			
			isoMsg.set(3,ISO8583_Mobile_Operator_ProcessingCode_Postpaid_Payment);
			isoMsg.set(4,toOperator.getAmount().toString());
			Timestamp ts = DateTimeUtil.getLocalTime();
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); 
			isoMsg.set(11,toOperator.getOriginalTransactionSTAN());
			
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); 
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); 
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); 
			
			if(!StringUtils.isBlank( toOperator.getISO8583_MerchantType()))
				isoMsg.set(18,toOperator.getISO8583_MerchantType());
			else
				isoMsg.set(18,FixToISOUtil.GetMerchantTypeBySourceApplication(toOperator.getSourceApplication()));
			
			isoMsg.set(32,constantFieldsMap.get("32"));
			isoMsg.set(37,toOperator.getISO8583_RetrievalReferenceNum());
			isoMsg.set(41,"11");
			
			if(!StringUtils.isBlank(toOperator.getProductIndicatorCode()))
				isoMsg.set(48,toOperator.getProductIndicatorCode());
			else
				isoMsg.set(48,"5100");
			isoMsg.set(49,ISO8583_Sinarmas_CurrencyCode_IDR.toString());
			isoMsg.set(61,toOperator.getDestMDN());
			String paddedSTAN = WrapperISOMessage.padOnLeft(toOperator.getOriginalTransactionSTAN(), '0', 6);
			String orig = "0200"+paddedSTAN;
//			IsoValue<Timestamp> isoValue = new IsoValue<Timestamp>(IsoType.DATE10, ts);
//			orig = orig + isoValue.toString();
			orig = orig+WrapperISOMessage.padOnLeft(toOperator.getISO8583_AcquiringInstIdCode().toString(),'0',11);
			isoMsg.set(90,orig);
			
		}catch (ISOException e) {
			log.error("PostpaidBillPaymentReversalToZTEProcessor :Error",e);
		}catch (Exception e) {
			log.error("PostpaidBillPaymentReversalToZTEProcessor :Error",e);
		}
		return isoMsg;
    }

}
