package com.mfino.iso8583.processor.bank.billpayments.fixtoiso;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPaymentToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.billpayments.UMGH2HISOMessage;

public class BillPayment extends BillPaymentBankRequest implements IFIXtoISOProcessor {

	public BillPayment() throws IOException {
		isoMsg = (UMGH2HISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200, CmFinoFIX.ISO8583_Variant_Bank_BillPayments_Gateway_Interface);
	}

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMBillPaymentToBank msg = (CMBillPaymentToBank) fixmsg;
		isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Bank_BillPayment_Payment0));
		Timestamp ts = msg.getTransferTime();
		isoMsg.setLocalTransactionDate(ts);
		isoMsg.setLocalTransactionTime(ts);
		isoMsg.setTransmissionTime(ts);
		isoMsg.setEncryptedPin(CryptographyService.buildEncryptedPINBlock16(msg.getPin(), msg.getSourceCardPAN(), null));// 52
		isoMsg.setTransactionRequestData(msg.getCustomerID());
		if(!StringUtils.isBlank(msg.getPaymentInquiryDetails()))
				isoMsg.setTransactionRequestData(msg.getPaymentInquiryDetails());
		String fee = "1000000106001C00000000C00000000C00000000C00000000C00000000C00000000C00000000C00000000C00000000C00000000C00000000C";
		String tf = msg.getTransactionFee().toString();
		fee = fee + WrapperISOMessage.padOnLeft(tf, '0', 8) + "C00000000";
		isoMsg.setTransactionFee(fee);
		isoMsg.setPayee(msg.getBillerProductCode());
		isoMsg.setAccountIdentification1(msg.getSourceCardPAN());
		return isoMsg;
	}
}
