package com.mfino.iso8583.processor.bank.billpayments.fixtoiso;

import java.io.IOException;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPaymentTopupReversalToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.billpayments.UMGH2HISOMessage;
import com.mfino.util.DateTimeUtil;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class BillPaymentTopupReversal extends BillPaymentBankRequest implements IFIXtoISOProcessor {

	public BillPaymentTopupReversal() throws IOException {
		isoMsg = (UMGH2HISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x420, CmFinoFIX.ISO8583_Variant_Bank_BillPayments_Gateway_Interface);
	}
	
	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMBillPaymentTopupReversalToBank msg = (CMBillPaymentTopupReversalToBank) fixmsg;
		isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_ProcessingCode_Bank_BillPayment_Topup_Reversal));
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setReservedF60Data("RESERVED DATA");
		String paddedSTAN = WrapperISOMessage.padOnLeft(msg.getBankSystemTraceAuditNumber(), '0', 6);
		String orig = "0200" + paddedSTAN;
		IsoValue<Timestamp> isoValue = new IsoValue<Timestamp>(IsoType.DATE10, ts);
		orig = orig + isoValue.toString();
		orig = orig + WrapperISOMessage.padOnLeft(CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(), '0', 11);
		orig = orig + WrapperISOMessage.padOnLeft(CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(), '0', 11);
		isoMsg.setOriginalPaymentTransactionData(orig);
		if (msg.getBillerProductCode() != null)
			isoMsg.setBillPayee(msg.getBillerProductCode());
		return isoMsg;
	}
}
