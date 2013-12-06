package com.mfino.iso8583.processor.bank.billpayments.fixtoiso;

import java.io.IOException;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPaymentTopupToBank;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.billpayments.UMGH2HISOMessage;

public class BillPaymentTopup extends BillPaymentBankRequest implements IFIXtoISOProcessor {

	public BillPaymentTopup() throws IOException {
		isoMsg = (UMGH2HISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200, CmFinoFIX.ISO8583_Variant_Bank_BillPayments_Gateway_Interface);
	}

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMBillPaymentTopupToBank msg = (CMBillPaymentTopupToBank) fixmsg;
		isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Bank_BillPayment_Topup0));
		isoMsg.setEncryptedPin(CryptographyService.buildEncryptedPINBlock16(msg.getPin(), msg.getSourceCardPAN(), null));// 52
		isoMsg.setTransactionRequestData(msg.getCustomerID());
		String fee = "1000000106001C00000000C00000000C00000000C00000000C00000000C00000000C00000000C00000000C00000000C00000000C00000000C";
		String tf = msg.getTransactionFee().toString();
		fee = fee + WrapperISOMessage.padOnLeft(tf, '0', 8) + "C00000000";
		isoMsg.setTransactionFee(fee);
		isoMsg.setPayee(msg.getBillerProductCode());
		isoMsg.setAccountIdentification1(msg.getSourceCardPAN());
		return isoMsg;
	}
}
