package com.mfino.fidelity.iso8583.processor.fixtoiso;

import java.math.BigDecimal;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.fidelity.iso8583.GetConstantCodes;
import com.mfino.fidelity.iso8583.utils.DateTimeFormatter;
import com.mfino.fidelity.iso8583.utils.FixToISOUtil;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

public class MoneyTransferReversalToFidelityProcessor extends FidelityISORequestProcessor {

	public MoneyTransferReversalToFidelityProcessor() {
		try {
			isoMsg.setMTI("1420");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		// super.process(fixmsg);

		CMMoneyTransferReversalToBank msg = (CMMoneyTransferReversalToBank)fixmsg;

		try {
			isoMsg.set(2, constantFieldsMap.get("2"));

//			String processingCode = GetConstantCodes.getTransactionType(msg);
			isoMsg.set(3, "401010"); // 3

			// TODO:
			BigDecimal amount = msg.getAmount().multiply(new BigDecimal(100));
			isoMsg.set(4, amount.longValue() + ""); // 4

			// create a new timestamp for the reversal as we cannot use the one
			// from moneytransferMessage
			Timestamp ts = DateTimeUtil.getLocalTime();
			Long transactionID = msg.getTransactionID();
			isoMsg.set(11, FixToISOUtil.getPaddedSTAN(transactionID.toString()));// 11
			isoMsg.set(12, DateTimeFormatter.getYYYYMMDDhhmmss(ts)); // Local Transaction Date & Time
			isoMsg.set(15, DateTimeFormatter.getYYYYMMDD(ts));	 // Date, settlement
			isoMsg.set(17, DateTimeFormatter.getYYYYMMDD(ts));	 // Capture Date
			isoMsg.set(24, "400"); 							 // Functioncode
			isoMsg.set(32, constantFieldsMap.get("32"));	// Code identifying the acquirer		
			
			isoMsg.set(37, msg.getTransactionID().toString());
			
			isoMsg.set(49, constantFieldsMap.get("49")); // 49

			String originalMsg;
			String AcquiringIdCode = isoMsg.getString("32");
			try {
	            AcquiringIdCode = FixToISOUtil.padOnLeft(AcquiringIdCode, '0', 11);
	            String ForwardingIdCode = isoMsg.getString("33");
	            ForwardingIdCode = FixToISOUtil.padOnLeft(ForwardingIdCode, '0', 11);
	            originalMsg = "1200"
	            		+ // orginal message MTI
	            		FixToISOUtil.getPaddedSTAN(msg.getBankSystemTraceAuditNumber().toString()) + DateTimeFormatter.getYYYYMMDDhhmmss(msg.getTransferTime())
	            		+ AcquiringIdCode ;
	            isoMsg.set(59, originalMsg); // 59
            }
            catch (Exception ex) {
	            // TODO Auto-generated catch block
	            ex.printStackTrace();
            }
			
			isoMsg.set(123, constantFieldsMap.get("123"));		//Delivery channel controller Id
			isoMsg.set(124, constantFieldsMap.get("124"));
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}
}
