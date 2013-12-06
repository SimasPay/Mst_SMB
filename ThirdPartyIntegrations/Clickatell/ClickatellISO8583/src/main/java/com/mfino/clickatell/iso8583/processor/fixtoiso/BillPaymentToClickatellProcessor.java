package com.mfino.clickatell.iso8583.processor.fixtoiso;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.clickatell.iso8583.utils.DateTimeFormatter;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPay;
import com.mfino.fix.CmFinoFIX.CMBillPayInquiry;
import com.mfino.fix.CmFinoFIX.CMBillPaymentInquiry;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferToOperator;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.util.DateTimeUtil;

public class BillPaymentToClickatellProcessor implements IFixToIsoProcessor {
	
	protected ISOMsg	          isoMsg	= new ISOMsg();
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	private static Integer PENDINGMTI= 201;
	public BillPaymentToClickatellProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			log.error("Error:",ex);
		}
	}
	
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		try{
			log.info("BillPaymentToClickatellProcessor :: process()");
			
			CMCommodityTransferToOperator toOperator = (CMCommodityTransferToOperator)fixmsg;
			Timestamp ts = DateTimeUtil.getLocalTime();
			long amount=toOperator.getAmount().longValue()*(100);
			Long transactionID = toOperator.getParentTransactionID();
			transactionID = transactionID % 1000000;
			String amountStr=String.valueOf(amount);
			if(PENDINGMTI.equals(toOperator.getSourceMsgType())){
				isoMsg.setMTI("0201");
				log.info("BillPaymentToClickatellProcessor :: Retriggering a pending transaction.");
			}
   			isoMsg.set(4,amountStr); //Amount
			isoMsg.set(7,DateTimeFormatter.getMMDDHHMMSS(ts)); //Transmission date and time
			
			if (toOperator.getISO8583_SystemTraceAuditNumber() != null)
				isoMsg.set(11,toOperator.getISO8583_SystemTraceAuditNumber()); //System Audit Number
			else
				isoMsg.set(11,transactionID.toString()); 
			
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); //Time Local Transaction.
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts));  //Date Local Transcation.
			isoMsg.set(15,toOperator.getDestMDN()); //Origin ID.
			isoMsg.set(18,toOperator.getTransactionID().toString()); //Client Transaction ID.
			isoMsg.set(19,toOperator.getProductIndicatorCode());//Product ID.
			
		}catch (ISOException ex) {
			log.error("BillPaymentToClickatellProcessor :Error",ex);
		}
		return isoMsg;
	}

	@Override
	public void setConstantFieldsMap(Map<String, String> arg0) {
		// TODO Auto-generated method stub
		
	}

}
